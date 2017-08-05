
// Willian Santos Silva

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.regex.Pattern;

public class Probabilistico {

	private static Map<String, HashSet<String>> vocabulario = new LinkedHashMap<String, HashSet<String>>();
	private static List<Integer> documentosSelecionados = new ArrayList<>();
	private static HashSet<String> consulta = new HashSet<String>();
	private static Map<String, Double> resultadosProb = new LinkedHashMap<String, Double>();

	private static Scanner scanner;

	public static String removerAcentos(String str) {
		String nfdNormalizedString = Normalizer.normalize(str, Normalizer.Form.NFD);
		Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
		return pattern.matcher(nfdNormalizedString).replaceAll("").replaceAll("[^a-zZ-Z1-9 ]", "");
	}

	// Verifica se String é Número
	public static boolean verificaString(String texto) {
		return texto.matches("[0-9]");
	}

	public static void lerArquivos(String arquivo, int documento) throws IOException {

		BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(arquivo), "ISO-8859-1"));
		String input;
		int proximoDoc = documento + 1;
		input = reader.readLine();
		HashSet<String> documentos = new LinkedHashSet<String>();

		do {

			if (input.equals(".I " + documento)) {

				String linhaAux1 = reader.readLine();
				while (linhaAux1 != null) {

					if (linhaAux1.equals(".W")) {

						String linhaAux = reader.readLine();
						while (linhaAux != null && !linhaAux.equals(".I " + proximoDoc)) {

							// Cria StringTokenizer para a frase de entrada
							StringTokenizer frase = new StringTokenizer(linhaAux);

							while (frase.hasMoreTokens()) {
								String palavra = frase.nextToken().toLowerCase();

								palavra = removerAcentos(palavra);

								if (!verificaString(palavra)) {

									if (vocabulario.containsKey(palavra)) {
										documentos = new LinkedHashSet<String>();
										documentos = vocabulario.get(palavra);
										documentos.add("Documento " + documento);
										vocabulario.put(palavra, documentos);
									} else {
										documentos = new LinkedHashSet<String>();
										documentos.add("Documento " + documento);
										vocabulario.put(palavra, documentos);
									}
								}
							}
							linhaAux = reader.readLine();
						}
						if (linhaAux == null || linhaAux.equals(".I " + proximoDoc))
							break;
					}
					linhaAux1 = reader.readLine();
				}
			}
			input = reader.readLine();

		} while (input != null);

		reader.close();
	}

	public static HashSet<String> lerArquivo(String arquivo, int documento) throws IOException {

		BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(arquivo), "ISO-8859-1"));
		String input;
		int proximoDoc = documento + 1;
		input = reader.readLine();
		HashSet<String> doc = new LinkedHashSet<String>();

		do {

			if (input.equals(".I " + documento)) {

				String linhaAux1 = reader.readLine();
				while (linhaAux1 != null) {

					if (linhaAux1.equals(".W")) {

						String linhaAux = reader.readLine();
						while (linhaAux != null && !linhaAux.equals(".I " + proximoDoc)) {

							// Cria StringTokenizer para a frase de entrada
							StringTokenizer frase = new StringTokenizer(linhaAux);

							while (frase.hasMoreTokens()) {
								String palavra = frase.nextToken().toLowerCase();

								palavra = removerAcentos(palavra);

								if (!verificaString(palavra)) {

									doc.add(palavra);
								}
							}
							linhaAux = reader.readLine();
						}
						if (linhaAux == null || linhaAux.equals(".I " + proximoDoc))
							break;
					}
					linhaAux1 = reader.readLine();
				}
			}
			input = reader.readLine();

		} while (input != null);

		reader.close();

		return doc;
	}

	public static void lerNomesDocumentos(String arquivo) throws IOException {

		// FileReader ler = new FileReader(arquivo);
		BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(arquivo), "ISO-8859-1"));
		String input;

		while ((input = reader.readLine()) != null) {

			// Cria StringTokenizer para a frase de entrada
			StringTokenizer frase = new StringTokenizer(input);

			while (frase.hasMoreTokens()) {
				String palavra = frase.nextToken();
				documentosSelecionados.add(Integer.parseInt(palavra));
			}

		}

		reader.close();

	}

	public static void lerConsulta(String consult) throws IOException {

		consulta = new HashSet<String>();

		StringTokenizer frase = new StringTokenizer(consult);

		while (frase.hasMoreTokens()) {
			String palavra = frase.nextToken().toLowerCase();

			palavra = removerAcentos(palavra);

			consulta.add(palavra);
		}

	}

	public static double calcularProb(String arquivo, int documento) throws IOException {

		List<String> list = new ArrayList<String>(consulta);
		int i = 0;
		double total = 0.0;

		HashSet<String> doc = lerArquivo(arquivo, documento);

		for (Map.Entry<String, HashSet<String>> entry : vocabulario.entrySet()) {
			if (i >= list.size())
				break;

			if (doc.contains(list.get(i))) {
				if (entry.getValue().size() > (documentosSelecionados.size() / 2.0)) {

					total += Math.log((documentosSelecionados.size() + 0.5) / (entry.getValue().size() + 0.5))
							/ Math.log(2.0);
				} else {

					total += Math.log((documentosSelecionados.size() - entry.getValue().size() + 0.5)
							/ (entry.getValue().size() + 0.5)) / Math.log(2.0);
				}
			}
			
			i++;
		}

		resultadosProb.put("Documento " + documento, total);

		return total;
	}

	public static List<Entry<String, Double>> ordenarDocumentos() {
		Set<Entry<String, Double>> set = resultadosProb.entrySet();
		List<Entry<String, Double>> list = new ArrayList<Entry<String, Double>>(set);
		Collections.sort(list, new Comparator<Map.Entry<String, Double>>() {
			public int compare(Map.Entry<String, Double> o1, Map.Entry<String, Double> o2) {
				return (o2.getValue()).compareTo(o1.getValue());
			}
		});

		return list;
	}

	public static void limpar() {
		vocabulario = new LinkedHashMap<String, HashSet<String>>();
		documentosSelecionados = new ArrayList<>();
		consulta = new HashSet<String>();
	}


	public static void main(String[] args) throws IOException {
		limpar();

		lerNomesDocumentos("documentos/documentosTodo.txt");

		for (Integer i : documentosSelecionados)
			lerArquivos("documentos/todo.txt", i);

		scanner = new Scanner(System.in);
		String consulta = scanner.nextLine();

		lerConsulta(consulta);

		for (Integer i : documentosSelecionados)
			calcularProb("documentos/todo.txt", i);

		for (Map.Entry<String, Double> entry : ordenarDocumentos()) {
			System.out.println(entry.getKey() + " " + entry.getValue().floatValue());
		}

	}

}
