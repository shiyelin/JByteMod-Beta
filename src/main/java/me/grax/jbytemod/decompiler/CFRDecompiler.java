package me.grax.jbytemod.decompiler;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Collection;
import java.util.HashMap;

import org.benf.cfr.reader.PluginRunner;
import org.benf.cfr.reader.api.ClassFileSource;
import org.benf.cfr.reader.bytecode.analysis.parse.utils.Pair;

import me.grax.jbytemod.JByteMod;
import me.grax.jbytemod.ui.DecompilerPanel;

public class CFRDecompiler extends Decompiler {

  public CFRDecompiler(JByteMod jbm, DecompilerPanel dp) {
    super(jbm, dp);
  }

  public String decompile(byte[] b) {
    try {
      HashMap<String, String> ops = new HashMap<>();
      ops.put("comments", "false");
      PluginRunner runner = new PluginRunner(ops, new ClassFileSource() {

        @Override
        public void informAnalysisRelativePathDetail(String a, String b) {
        }

        @Override
        public String getPossiblyRenamedPath(String path) {
          return path;
        }

        @Override
        public Pair<byte[], String> getClassFileContent(String path) throws IOException {
          String name = path.substring(0, path.length() - 6);
          if (name.equals(cn.name)) {
            return Pair.make(b, name);
          }
          return null; //cfr loads unnecessary classes
        }

        @Override
        public Collection<String> addJar(String arg0) {
          throw new RuntimeException();
        }
      });
      String decompilation = runner.getDecompilationFor(cn.name);
      System.gc(); //cfr has a performance bug
      return decompilation.substring(37); //small hack to remove watermark
    } catch (Exception e) {
      e.printStackTrace();
      StringWriter sw = new StringWriter();
      PrintWriter pw = new PrintWriter(sw);
      e.printStackTrace(pw);
      return sw.toString();
    }
  }

  protected Pair<byte[], String> getSystemClass(String name, String path) throws IOException {
    InputStream is = ClassLoader.getSystemClassLoader().getResourceAsStream(path);
    if (is != null) {
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      byte[] buffer = new byte[4096];
      int n;
      while ((n = is.read(buffer)) > 0) {
        baos.write(buffer, 0, n);
      }
      return Pair.make(baos.toByteArray(), name);
    }
    return null;
  }
}
