package hello.dcsms.omzen.Util;

import hello.dcsms.omzen.theme.ThemeKontsran;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import android.util.Log;

public class ZipUtils {
	private static final int BUFFER_SIZE = 1024;

	/*
	 * 
	 * Zips a file at a location and places the resulting zip file at the
	 * toLocation Example: zipFileAtPath("downloads/myfolder",
	 * "downloads/myFolder.zip");
	 */

	public static boolean zipFileAtPath(String[] sourcePaths, String toLocation) {
		// ArrayList<String> contentList = new ArrayList<String>();

		try {
			BufferedInputStream origin = null;
			FileOutputStream dest = new FileOutputStream(toLocation);
			ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(
					dest));
			for (String sourcePath : sourcePaths) {
				File sourceFile = new File(sourcePath);
				if (sourceFile.isDirectory()) {
					zipSubFolder(out, sourceFile, sourceFile.getParent()
							.length());
				} else {
					byte data[] = new byte[BUFFER_SIZE];
					FileInputStream fi = new FileInputStream(sourcePath);
					origin = new BufferedInputStream(fi, BUFFER_SIZE);
					ZipEntry entry = new ZipEntry(
							getLastPathComponent(sourcePath));
					out.putNextEntry(entry);
					int count;
					while ((count = origin.read(data, 0, BUFFER_SIZE)) != -1) {
						out.write(data, 0, count);
					}
				}
			}
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}

	/*
	 * 
	 * Zips a subfolder
	 */

	private static void zipSubFolder(ZipOutputStream out, File folder,
			int basePathLength) throws IOException {

		File[] fileList = folder.listFiles();
		BufferedInputStream origin = null;
		for (File file : fileList) {
			if (file.isDirectory()) {
				zipSubFolder(out, file, basePathLength);
			} else {
				byte data[] = new byte[BUFFER_SIZE];
				String unmodifiedFilePath = file.getPath();
				String relativePath = unmodifiedFilePath
						.substring(basePathLength);
				Log.i("ZIP SUBFOLDER", "Relative Path : " + relativePath);
				FileInputStream fi = new FileInputStream(unmodifiedFilePath);
				origin = new BufferedInputStream(fi, BUFFER_SIZE);
				ZipEntry entry = new ZipEntry(relativePath);
				out.putNextEntry(entry);
				int count;
				while ((count = origin.read(data, 0, BUFFER_SIZE)) != -1) {
					out.write(data, 0, count);
				}
				origin.close();
			}
		}
	}

	/*
	 * gets the last path component
	 * 
	 * Example: getLastPathComponent("downloads/example/fileToZip"); Result:
	 * "fileToZip"
	 */
	public static String getLastPathComponent(String filePath) {
		String[] segments = filePath.split("/");
		String lastPathComponent = segments[segments.length - 1];
		return lastPathComponent;
	}

	public static void zip(String[] files, String zipFile) throws IOException {
		BufferedInputStream origin = null;
		ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(
				new FileOutputStream(zipFile)));
		try {
			byte data[] = new byte[BUFFER_SIZE];

			for (int i = 0; i < files.length; i++) {
				FileInputStream fi = new FileInputStream(files[i]);
				origin = new BufferedInputStream(fi, BUFFER_SIZE);
				try {
					ZipEntry entry = new ZipEntry(files[i].substring(files[i]
							.lastIndexOf("/") + 1));
					out.putNextEntry(entry);
					int count;
					while ((count = origin.read(data, 0, BUFFER_SIZE)) != -1) {
						out.write(data, 0, count);
					}
				} finally {
					origin.close();
				}
			}
		} finally {
			out.close();
		}
	}

	private static boolean isnamafileexist(String namafile, String[] nama) {
		boolean result = false;
		for (String s : nama) {
			if (namafile.contains(s))
				result = true;

		}
		return result;
	}

	public static Boolean unzip(String[] namafileygdicrot, String zipFile,
			String location) {
		boolean result = false;
		int size;
		byte[] buffer = new byte[BUFFER_SIZE];

		try {
			if (!location.endsWith("/")) {
				location += "/";
			}
			File f = new File(location);
			if (!f.isDirectory()) {
				f.mkdirs();
			}
			ZipInputStream zin = new ZipInputStream(new BufferedInputStream(
					new FileInputStream(zipFile), BUFFER_SIZE));
			try {
				ZipEntry ze = null;
				while ((ze = zin.getNextEntry()) != null) {
					String path = location + ze.getName();
					File unzipFile = new File(path);

					if (ze.isDirectory()) {
						if (!unzipFile.isDirectory()) {
							unzipFile.mkdirs();
						}
					} else {
						// check for and create parent directories if they don't
						// exist
						File parentDir = unzipFile.getParentFile();
						if (null != parentDir) {
							if (!parentDir.isDirectory()) {
								parentDir.mkdirs();
							}
						}
						if (!unzipFile.getName().equals("info.json")
								&& !unzipFile.getName().contains("screenshot")
								&& isnamafileexist(unzipFile.getName(),
										namafileygdicrot)) {
							// unzip the file
							FileOutputStream out = new FileOutputStream(
									unzipFile, false);
							BufferedOutputStream fout = new BufferedOutputStream(
									out, BUFFER_SIZE);
							try {
								while ((size = zin.read(buffer, 0, BUFFER_SIZE)) != -1) {
									fout.write(buffer, 0, size);
								}

								zin.closeEntry();
							} finally {
								fout.flush();
								fout.close();
							}
						}
					}
				}
				result = true;
			} finally {
				zin.close();
			}
		} catch (Exception e) {
			Log.e("", "Unzip exception", e);
			result = false;
		}
		return result;
	}

	public static Boolean unzip(String zipFile, String location) {
		boolean result = false;
		int size;
		byte[] buffer = new byte[BUFFER_SIZE];

		try {
			if (!location.endsWith("/")) {
				location += "/";
			}
			File f = new File(location);
			if (!f.isDirectory()) {
				f.mkdirs();
			}
			ZipInputStream zin = new ZipInputStream(new BufferedInputStream(
					new FileInputStream(zipFile), BUFFER_SIZE));
			try {
				ZipEntry ze = null;
				while ((ze = zin.getNextEntry()) != null) {
					String path = location + ze.getName();
					File unzipFile = new File(path);

					if (ze.isDirectory()) {
						if (!unzipFile.isDirectory()) {
							unzipFile.mkdirs();
						}
					} else {
						// check for and create parent directories if they don't
						// exist
						File parentDir = unzipFile.getParentFile();
						if (null != parentDir) {
							if (!parentDir.isDirectory()) {
								parentDir.mkdirs();
							}
						}
						if (!unzipFile.getName().equals("info.json")
								&& !unzipFile.getName().contains("screenshot")) {
							// unzip the file
							FileOutputStream out = new FileOutputStream(
									unzipFile, false);
							BufferedOutputStream fout = new BufferedOutputStream(
									out, BUFFER_SIZE);
							try {
								while ((size = zin.read(buffer, 0, BUFFER_SIZE)) != -1) {
									fout.write(buffer, 0, size);
								}

								zin.closeEntry();
							} finally {
								fout.flush();
								fout.close();
							}
						}
					}
				}
				result = true;
			} finally {
				zin.close();
			}
		} catch (Exception e) {
			Log.e("", "Unzip exception", e);
			result = false;
		}
		return result;
	}
}
