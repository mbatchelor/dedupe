package com.pentaho.dedupe;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.pentaho.dedupe.args.Arg;
import com.pentaho.dedupe.args.ArgParser;
import com.pentaho.dedupe.args.flags.StandardFlag;
import com.pentaho.dedupe.args.parsers.BooleanParser;
import com.pentaho.dedupe.args.parsers.FolderParser;
import com.pentaho.dedupe.args.parsers.StringParser;
import com.pentaho.dedupe.containers.Pair;
import com.pentaho.dedupe.containers.Pair.HashImpl;
import com.pentaho.dedupe.util.FileUtil;

public class Dedupe {
  private static final HashImpl<String, File> HASH_IMPL = new HashImpl<String, File>() {

    @SuppressWarnings("unchecked")
    @Override
    public boolean equals(Pair<String, File> first, Object second) {
      return first.getFirst().equals(((Pair<String, File>) second).getFirst());
    }

    @Override
    public int hashCode(Pair<String, File> pair) {
      return pair.getFirst().hashCode();
    }
  };

  private final File rootDirectory;

  private final File dedupFolder;

  private final File thisFile;

  public Dedupe(File rootDirectory, File dedupFolder) throws URISyntaxException {
    this.rootDirectory = rootDirectory.getAbsoluteFile();
    this.dedupFolder = dedupFolder.getAbsoluteFile();
    thisFile = new File(Dedupe.class.getProtectionDomain().getCodeSource().getLocation().toURI());
  }

  private Set<Pair<String, File>> getAllFiles(URI rootUri, File rootDirectory) {
    Set<Pair<String, File>> result = new HashSet<Pair<String, File>>();
    if (!rootDirectory.getAbsoluteFile().equals(dedupFolder)) {
      File[] files = rootDirectory.listFiles();
      if (files != null) {
        for (File file : files) {
          if (file.isDirectory()) {
            result.addAll(getAllFiles(rootUri, file));
          } else if (!file.getAbsoluteFile().equals(thisFile)) {
            result.add(new Pair<String, File>(rootUri.relativize(file.toURI()).toString(), file.getAbsoluteFile(),
                HASH_IMPL));
          }
        }
      }
    }
    return result;
  }

  public void dedupe(boolean preserveExtensions) throws NoSuchAlgorithmException, IOException {
    if (dedupFolder.exists()) {
      throw new IOException("Dedup output folder exists already, refusing to continue.");
    }
    dedupFolder.mkdirs();
    MessageDigest md = MessageDigest.getInstance("SHA1");
    Set<Pair<String, File>> pairs = getAllFiles(rootDirectory.toURI(), rootDirectory);
    List<String> mappings = new ArrayList<String>(pairs.size());
    for (Pair<String, File> pair : pairs) {
      String dedupFileName = FileUtil.getHexDigest(md, pair.getSecond());
      if (preserveExtensions) {
        String fileName = pair.getSecond().getName();
        int index = fileName.indexOf('.');
        if (index >= 0) {
          dedupFileName += fileName.substring(index);
        }
      }
      File dedupFile = new File(dedupFolder.toURI().resolve(dedupFileName));
      if (!dedupFile.exists()) {
        if (!pair.getSecond().renameTo(dedupFile)) {
          throw new IOException("Couldn't move " + pair.getSecond() + " to " + dedupFile);
        }
      } else if (!pair.getSecond().delete()) {
        throw new IOException("Couldn't delete " + pair.getSecond());
      }
      mappings.add(pair.getFirst() + "\n" + dedupFileName);
    }
    FileWriter fileWriter = new FileWriter(dedupFolder.getAbsolutePath() + "/manifest");
    try {
      for (String mapping : mappings) {
        fileWriter.write(mapping);
        fileWriter.write("\n");
      }
    } finally {
      fileWriter.close();
    }
  }

  public void redupe() throws IOException, URISyntaxException {
    if (!dedupFolder.exists()) {
      throw new IOException("Dedup output folder doesn't exist, refusing to continue.");
    }
    Map<String, List<String>> outputMap = new HashMap<String, List<String>>();
    File manifest = new File(dedupFolder.toURI().resolve("manifest"));
    FileReader fileReader = new FileReader(manifest);
    BufferedReader br = null;
    try {
      br = new BufferedReader(fileReader);
      String line;
      while ((line = br.readLine()) != null) {
        if (line.trim().length() > 0) {
          String path = line;
          String sha1 = br.readLine();
          List<String> paths = outputMap.get(sha1);
          if (paths == null) {
            paths = new ArrayList<String>();
            outputMap.put(sha1, paths);
          }
          paths.add(path);
        }
      }
    } finally {
      if (br != null) {
        br.close();
      }
      fileReader.close();
    }
    for (Entry<String, List<String>> entry : outputMap.entrySet()) {
      String fileName = entry.getKey();
      File sha1File = new File(dedupFolder.toURI().resolve(fileName));
      List<String> paths = entry.getValue();
      for (int i = 0; i < paths.size() - 1; i++) {
        FileUtil.copy(sha1File, new File(rootDirectory.toURI().resolve(paths.get(i))));
      }
      if (!sha1File.renameTo(new File(rootDirectory.toURI().resolve(paths.get(paths.size() - 1))))) {
        throw new IOException("Unable to rename " + sha1File + " to " + paths.get(paths.size() - 1));
      }
    }
    manifest.delete();
    dedupFolder.delete();
  }

  public static void main(String[] args) throws NoSuchAlgorithmException, IOException, URISyntaxException {
    ArgParser argParser = new ArgParser();
    Arg<File> rootDir = argParser.register(new StandardFlag("r", "rootDir", "The root directory to deduplicate"),
        new FolderParser(), new File("").getAbsoluteFile());
    Arg<File> dedupeDir = argParser.register(new StandardFlag("d", "dedupeDir",
        "The directory to store the deduplication, rootDir/dedupe by default"), new FolderParser());
    Arg<String> operation = argParser.register(new StandardFlag("o", "operation",
        "The operation to perform (either dedupe or redupe)"), new StringParser(), "redupe");
    Arg<Boolean> preserveExtensions = argParser.register(new StandardFlag("p", "preserveExtensions",
        "Preserve the extension of the files, only relevant during deduplication"), new BooleanParser(), false);
    argParser.parse(args);
    if (dedupeDir.get() == null) {
      dedupeDir.process(new File(rootDir.get().toURI().resolve("dedupe")).getAbsolutePath());
    }
    Dedupe dedupe = new Dedupe(rootDir.get(), dedupeDir.get());
    if (operation.get().equals("dedupe")) {
      dedupe.dedupe(preserveExtensions.get());
    } else if (operation.get().equals("redupe")) {
      dedupe.redupe();
    } else {
      throw new RuntimeException("Operation must be either dedupe or redupe");
    }
  }
}
