/*
 * ClassManager - Supervision de classes et Laboratoire de langue
 * Copyright (C) 2013 Fabrice Alleau <fabrice.alleau@siclic.fr>
 *
 * This file is part of ClassManager.
 *
 * ClassManager is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * ClassManager is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with ClassManager.  If not, see <http://www.gnu.org/licenses/>.
 */
package thot.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;

import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

/**
 * Utilitaires pour les archipe Zip.
 *
 * @author Fabrice Alleau
 * @version 1.90
 */
public class ZipUtilities {

    /**
     * Taille du buffer.
     */
    private static int BUFFER_SIZE = 32768;

    /**
     * Décompresse un fichier zippé dans un répertoire.
     *
     * @param srcFile le fichier zippé.
     * @param directory le répertoire au décompressé.
     * @throws IOException
     */
    public static void unzipFileIntoDirectory(File srcFile, File directory)
            throws IOException {

        ZipFile zipFile = new ZipFile(srcFile);
        Enumeration<? extends ZipEntry> files = zipFile.entries();
        File file;
        FileOutputStream outputStream = null;

        ZipEntry entry;
        InputStream inputStream;
        while (files.hasMoreElements()) {
            try {
                entry = files.nextElement();
                inputStream = zipFile.getInputStream(entry);
                file = new File(directory.getAbsolutePath(), entry.getName());

                if (entry.isDirectory()) {
                    file.mkdirs();
                    continue;
                } else {
                    file.getParentFile().mkdirs();
                    file.createNewFile();
                }

                outputStream = new FileOutputStream(file);
                streamCopy(inputStream, outputStream);
            } finally {
                if (outputStream != null) {
                    try {
                        outputStream.close();
                    } catch (IOException e) {
                        // ignore
                    }
                }
            }
        }
    }

    /**
     * Ajoute un fichier (ou tous les fichiers contenu dans le répertoire) dans
     * fichier zippé.
     *
     * @param file le fichier ou le répertoire.
     * @param zipFile le fichier zip.
     * @param compression mode de compression.
     * @throws IOException
     */
    public static void fileToZip(File file, File zipFile, boolean compression)
            throws IOException {

        int compressionMethod = compression ? ZipOutputStream.DEFLATED : ZipOutputStream.STORED;
        zipFile.createNewFile();
        FileOutputStream fout = new FileOutputStream(zipFile);
        try (ZipOutputStream zout = new ZipOutputStream(fout)) {
            zout.setMethod(compressionMethod);
            zout.setLevel(9);
            if (file.isDirectory()) {
                File[] files = file.listFiles();
                for (File childFile : files) {
                    fileToZip(childFile, zout, file);
                }
            } else if (file.isFile()) {
                fileToZip(file, zout, file.getParentFile());
            }
        }
    }

    /**
     * Ajoute un fichier (ou tous les fichiers contenu dans le répertoire) dans
     * fichier zippé.
     *
     * @param file le fichier ou le répertoire.
     * @param zout le flux du fichier de compression.
     * @param baseDir le répertoire premier pour les ZipEntry aillent un chemin
     * relatifs.
     * @throws IOException
     */
    private static void fileToZip(File file, ZipOutputStream zout, File baseDir)
            throws IOException {

        String entryName = file.getPath().substring(baseDir.getPath().length() + 1);
        if (File.separatorChar != '/') {
            entryName = entryName.replace(File.separator, "/");
        }
        if (file.isDirectory()) {
            zout.putNextEntry(new ZipEntry(entryName + "/"));
            zout.closeEntry();
            File[] files = file.listFiles();
            for (File childFile : files) {
                fileToZip(childFile, zout, baseDir);
            }
        } else {
            FileInputStream inputStream = null;
            try {
                inputStream = new FileInputStream(file);
                zout.putNextEntry(new ZipEntry(entryName));
                streamCopy(inputStream, zout);
            } finally {
                zout.closeEntry();
                if (inputStream != null) {
                    inputStream.close();
                }
            }
        }
    }

    /**
     * Copie le flux de lecture d'un fichier dans le flux d'écriture de zip.
     *
     * @param is le flux de données
     * @param os le flux de sortie.
     * @throws IOException
     */
    private static void streamCopy(InputStream is, OutputStream os) throws IOException {
        byte[] buffer = new byte[BUFFER_SIZE];
        int len = is.read(buffer);
        while (len > 0) {
            os.write(buffer, 0, len);
            len = is.read(buffer);
        }
    }
}