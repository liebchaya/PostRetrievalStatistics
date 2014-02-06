package index;
import java.io.File;
import java.io.FileFilter;
import java.io.PrintStream;
import java.util.Vector;

import lemurproject.indri.IndexEnvironment;
import lemurproject.indri.IndexStatus;
import lemurproject.indri.Specification;


public class IndriIndexCorpora {

	
	public void index(String iname, String docpath) {
		int totalDocumentsIndexed=0;
		  
		// iname is an absolute path
		File idx = new File(iname);
		
		// construct IndexEnvironment
		// set parameters
		// go.
		IndexEnvironment env = new IndexEnvironment();
		UIIndexStatus stat = new UIIndexStatus();
		try {
		
	        // memory 1024 M
			long retval = 1024000000;
	        env.setMemory(retval);
	        
          String fileClass = "txt";
          String fields = "docno";
          String [] metafields = fields.split(",");;

          // this needs to address the forward/backward/metadata distinction.
          env.setMetadataIndexedFields(metafields, metafields);
          Specification spec = env.getFileClassSpec(fileClass);
          java.util.Vector vec = new java.util.Vector();
          java.util.Vector incs = null;
          if (spec.include.length > 0)
              incs = new java.util.Vector();
          
          // metadata fields.
          for (int i = 0; i < spec.metadata.length; i++)
              vec.add(spec.metadata[i]);
          for (int i = 0; i < metafields.length; i++) {	    
              if (vec.indexOf(metafields[i]) == -1)
            vec.add(metafields[i]);
              // add to include tags only if there were some already.
              if (incs != null && incs.indexOf(metafields[i]) == -1)
            incs.add(metafields[i]);
          }

          if (vec.size() > spec.metadata.length) {
              // we added something.
              spec.metadata = new String[vec.size()];
              vec.copyInto(spec.metadata);
          }
          // update include if needed.
          if (incs != null && incs.size() > spec.include.length) {
              spec.include = new String[incs.size()];
              incs.copyInto(spec.include);
          }
          // update the environment.
          env.addFileClass(spec);

          
          File docPath = new File(docpath);
          String [] datafiles = formatDataFiles(docPath);

          // create a new empty index (after parameters have been set).
          env.create(idx.getAbsolutePath(), stat);
          
          // do the building.
          for (int i = 0; i < datafiles.length; i++){
              String fname = datafiles[i];
              env.addFile(fname, fileClass);
              env.documentsIndexed();
          }
          env.close();

	}catch (Exception e) {
		e.printStackTrace();
	}
	}
		
	
		/** Create the datafiles list of strings.
		@return The list of files
	    **/
	    private String[] formatDataFiles(File docPath) {
	    	// handle directories, recursion, filename patterns	
			Vector accumulator = new Vector();
			String [] retval = new String[0];

		  
			FileFilter filt = null;
			final String regexp = "*.txt";
			if (regexp.length() > 0) {
			    final String filtString = encodeRegexp(regexp);
			    filt = new FileFilter() {
				    public boolean accept(File thisfile) {
					String name = thisfile.getName().toLowerCase();
					return (thisfile.isDirectory() ||
						name.matches(filtString));
				    }
				};
			}
			
			formatDataFiles(docPath, filt, accumulator);		
			
			retval = (String[]) accumulator.toArray(retval);
			return retval;
	}

		/** Accumulate filenames for the input list.
		If the File is a directory, iterate over all of the files
		in that directory that satisfy the filter. If recurse into
		subdirectories is selected and the File is a directory, 
		invoke recursivly on on all directories within the directory.
		@param accum Vector to accumulate file names recusively.
		@param file a File (either a file or directory)
		@param f the filename filter to apply.
	    */
		
	    private void formatDataFiles(File file, FileFilter f, Vector accum) {
	      if (file.isDirectory()) {
	          // handle directory
	          File [] files = file.listFiles(f);
	          for (int i = 0; i < files.length; i++) {
	        if (files[i].isDirectory()) {
	              formatDataFiles(files[i], f, accum);
	        } else {
	            accum.add(files[i].getAbsolutePath());
	        }
	          }
	      } else {
	          accum.add(file.getAbsolutePath());

	      }
	    }
	    
	    private static String encodeRegexp(String regexp) {
	    	// rewrite shell fname pattern to regexp.
	    	// * -> .*
	    	// ? -> .?
	    	// Add ^,$
	    	// . -> \.
	    	String retval = "^" + regexp + "$";
	    	retval = retval.replaceAll("\\.", "\\.");
	    	retval = retval.replaceAll("\\*", ".*");
	    	retval = retval.replaceAll("\\?", ".?");
	    	return retval;
	        }

}

class UIIndexStatus extends IndexStatus {
	public void status(int code, String documentFile, String error, 
			   int documentsIndexed, int documentsSeen) {
	  
		PrintStream messages = System.out;
		if (code == action_code.FileOpen.swigValue()) {
		messages.append("Documents: " + documentsIndexed + "\n");
		messages.append("Opened " + documentFile + "\n");
	    } else if (code == action_code.FileSkip.swigValue()) {
		messages.append("Skipped " + documentFile + "\n");
	    } else if (code == action_code.FileError.swigValue()) {
		messages.append("Error in " + documentFile + " : " + error + 
				"\n");
	    } else if (code == action_code.DocumentCount.swigValue()) {
//		if( (documentsIndexed % 500) == 0)
		    messages.append( "Documents: " + documentsIndexed + "\n" );
	    }
	}
    }

