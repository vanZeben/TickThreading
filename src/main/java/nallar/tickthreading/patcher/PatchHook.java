package nallar.tickthreading.patcher;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;

import nallar.log.PatchLog;

public class PatchHook {
	private static Patcher patcher;

	static {
		PatchLog.fine("PatchHook running under classloader " + PatchHook.class.getClassLoader().getClass().getName());
		try {
			Class<?> clazz = Class.forName("cpw.mods.fml.relauncher.ServerLaunchWrapper");
			try {
				Field field = clazz.getDeclaredField("startupArgs");
				field.set(null, PatchLauncher.startupArgs);
			} catch (NoSuchFieldException ignored) {
			}
		} catch (Throwable t) {
			PatchLog.severe("Failed to set up MCPC+ startup args. This is only a problem if you are using MCPC+", t);
		}
		try {
			patcher = new Patcher(getPatchFileStream(), Patches.class);
		} catch (Throwable t) {
			PatchLog.severe("Failed to create Patcher", t);
			System.exit(1);
		}
	}

	public static FileInputStream getPatchFileStream() {
	  File file = new File("./config/TickThreadingPatches.xml");
	  if (!file.exists()) {
	    InputStream stream = null;
	    OutputStream resStreamOut = null;
	    try {
	      file.createNewFile();
	      stream = PatchLauncher.class.getResourceAsStream("/patches.xml");
	      if (stream == null) {
	        return new FileInputStream(file);
	      }
	      int readBytes;
	      byte[] buffer = new byte[4096];
	      resStreamOut = new FileOutputStream(file);
	      while ((readBytes = stream.read(buffer)) > 0) {
	        resStreamOut.write(buffer, 0, readBytes);
	      }
	    } catch (IOException e) {
	      e.printStackTrace();
	    } finally {
	      try {
	        if (stream != null) {
	          stream.close();
	        }
	        if (resStreamOut != null) {
	          resStreamOut.close();
	        }
	      } catch (IOException e) {
	        e.printStackTrace();
	      }
	    }
	  }
	  try {
	    return new FileInputStream(file);
	  } catch (FileNotFoundException e) {
	    return null;
	  }
	}
	
	public static byte[] preSrgTransformationHook(String name, String transformedName, byte[] originalBytes) {
		try {
			return patcher.preSrgTransformation(name, transformedName, originalBytes);
		} catch (Throwable t) {
			PatchLog.severe("Failed to patch " + transformedName, t);
		}
		return originalBytes;
	}

	public static byte[] postSrgTransformationHook(String name, String transformedName, byte[] originalBytes) {
		try {
			return patcher.postSrgTransformation(name, transformedName, originalBytes);
		} catch (Throwable t) {
			PatchLog.severe("Failed to patch " + transformedName, t);
		}
		return originalBytes;
	}

	public static boolean requiresSrgHook(String transformedName) {
		return patcher.shouldPostSrgTransform(transformedName);
	}
}
