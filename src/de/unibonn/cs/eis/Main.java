/**
 * 
 */
package de.unibonn.cs.eis;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.Scanner;

import scala.collection.Iterator;
import scala.collection.Seq;
import scala.collection.immutable.List;
import edu.knowitall.openie.Instance;
import edu.knowitall.openie.OpenIE;
import edu.knowitall.tool.parse.ClearParser;
import edu.knowitall.tool.postag.OpenNlpPostagger;
import edu.knowitall.tool.srl.ClearSrl;
import edu.knowitall.tool.tokenize.OpenNlpTokenizer;

/**
 * @author kemele
 *
 */
public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
				//String[] a = new String[]{"Obama was elected to the Illinois Senate in 1996, succeeding Democratic State Senator Alice Palmer as Senator from Illinois's 13th District, which at that time spanned Chicago South Side neighborhoods from Hyde Park–Kenwood south to South Shore and west to Chicago Lawn. Once elected, Obama gained bipartisan support for legislation that reformed ethics and health care laws.[53] He sponsored a law that increased tax credits for low-income workers, negotiated welfare reform, and promoted increased subsidies for childcare.[54] In 2001, as co-chairman of the bipartisan Joint Committee on Administrative Rules, Obama supported Republican Governor Ryan's payday loan regulations and predatory mortgage lending regulations aimed at averting home foreclosures."};
				// new ClearSrl();
				//URL url = new URL("https://en.wikipedia.org/wiki/Barack_Obama");

				ClearParser par = new ClearParser(new OpenNlpPostagger(new OpenNlpTokenizer()));
				Scanner text = new Scanner(new FileInputStream(new File("wikitext.txt")));
				//StringBuffer buffer = new StringBuffer();
				
				Scanner lexicon = new Scanner(new FileInputStream(new File("lexicon.txt")));
				String[][] lex = new String[10][2];
				int p =0;
				while(lexicon.hasNext()){
					String line = lexicon.nextLine();
					lex[p++] = line.split(",");
				}
				lexicon.close();
				OpenIE openie = new OpenIE(par, new ClearSrl(), false);
				PrintStream out = new PrintStream(new FileOutputStream(new File("all.txt")), true);
				while(text.hasNext()){				
				String line = text.nextLine();
				//System.out.println(line);
				Seq<Instance> i =  openie.extract(line);
				
				List<Instance> li = i.toList();
				 Iterator<Instance> it = li.toIterator();
				while(it.hasNext()){
					Instance ins = it.next();
					//System.out.println("Confidence=" + ins.confidence());
				//	System.out.println(ins.extr());	
					//System.out.println(ins.extr().tripleString().replace('(', ' ').replace(')', ' '));
					String[] rel = ins.extr().tripleString().replace('(', ' ').replace(')', ' ').split(";");
					if(rel.length < 3 ){
						continue;
					}
					boolean b = false;
					for(int k =0;k<rel.length;k++){
						rel[k] = rel[k].trim();
						int l = rel[k].split(" ").length;
						if(rel[k].length() == 0 || l == 0 || l  > 5){
							b = true;
							break;
						}
					} 
					if(b){
						continue;
					}
					for(int j=0; j<lex.length; j++){
						if(rel[0].toLowerCase().contains(lex[j][0].toLowerCase()) || rel[2].toLowerCase().contains(lex[j][0].toLowerCase()) ){
							System.out.println("Confidence=" + ins.confidence());
							/*String ttt = "";
							for(int k =0;k<rel.length;k++){
								System.out.println(rel[k]);
								ttt +=rel[k].trim() + " ";
							}
							if(ttt.length()>0){
								ttt+=" .";
							}*/
							out.println(ins.extr().tripleString().replace('(', ' ').replace(')', ' ') +" .");
							break;
						}
					  }
					}
				}
				out.close();
				text.close();
	}

}
