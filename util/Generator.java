package cloudtestingdi.util;


import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Class that creates suites testecase
 * 
 * @author Gustavo Sávio <saviojp@gmail.com>
 * 
 */
public class Generator {

	public static void create() throws IOException {
		File arq = new File(System.getProperty("user.dir") + "/src",
				"UnitStress.java");
		arq.createNewFile();

		FileWriter fileWriter = new FileWriter(arq);
		PrintWriter printWriter = new PrintWriter(fileWriter);

		printWriter.println("import junit.framework.TestCase;");
		printWriter.println("");

		printWriter.println("import org.junit.Test;");
		printWriter.println("");

		printWriter.println("public class UnitStress extends TestCase { ");
		printWriter.println("");

		printWriter.println("\t public UnitStress() {");
		printWriter.println("");
		printWriter.println("\t }");
		printWriter.println("");

		printWriter.println("\t public UnitStress(String string ) {");
		printWriter.println("\t \t super(string);");
		printWriter.println("\t}");
		printWriter.println("");

		for (int i = 0; i < 1800; i++) {
			printWriter.println("\t @Test");
			printWriter.println("\t public void testAdd" + i + "()  {");
			printWriter.println("\t \t long amostras = 5000000;");
			printWriter.println("\t \t long pontosDentroDoCirculo = 0;");
			printWriter.println("");
			printWriter.println("\t \t for (int i = 0; i < amostras; i++) {");
			printWriter.println("\t \t \t double px = 2 * Math.random() - 1;");
			printWriter.println("\t \t \t double py = 2 * Math.random() - 1;");
			printWriter.println("");
			printWriter
					.println("\t \t \t if (Math.pow(px, 2) + Math.pow(py, 2) <= 1) {");
			printWriter.println("\t \t \t \t pontosDentroDoCirculo++;");
			printWriter.println("\t \t \t }");
			printWriter.println("\t \t }");
			printWriter.println("");
			printWriter.println("\t \t try {");
			printWriter.println("\t \t \t Thread.sleep(200);");
			printWriter.println("\t \t } catch (Exception e) {");
			printWriter.println("\t \t \t e.printStackTrace();");
			printWriter.println("\t \t }");
			printWriter.println("");
			printWriter
					.println("\t \t assertEquals(314, Math.round(((4 * (double) pontosDentroDoCirculo / (double) amostras) * 100)/ 1d));");
			printWriter.println("\t }");
			printWriter.println("");
		}
		printWriter.println("}");
		printWriter.flush();
		printWriter.close();
	}

	public static void main(String[] args) {
		try {
			Generator.create();
			System.out.println("Done!");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}