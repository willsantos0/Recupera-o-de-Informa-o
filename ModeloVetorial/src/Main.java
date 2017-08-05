
// Willian Santos Silva - 11411BSI238

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.regex.Pattern;

public class Main {

	private static Map<String, Integer> vocabulario = new LinkedHashMap<String, Integer>();
	private static Map<String, Double> valoresTf = new LinkedHashMap<String, Double>();
	private static Map<String, Double> valoresIdf = new LinkedHashMap<String, Double>();
	private static Map<String, Double> valoresTfIdf = new LinkedHashMap<String, Double>();

	private static Map<String, Double> valoresTfConsulta = new LinkedHashMap<String, Double>();
	private static Map<String, Double> valoresTfIdfConsulta = new LinkedHashMap<String, Double>();
	private static Map<String, Double> similaridadeDocumentos = new LinkedHashMap<String, Double>();

	private static Scanner scanner;

	public static String removerAcentos(String str) {
		String nfdNormalizedString = Normalizer.normalize(str, Normalizer.Form.NFD);
		Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
		return pattern.matcher(nfdNormalizedString).replaceAll("").replaceAll("[^a-zZ-Z1-9 ]", "");
	}

	public static Map<String, Integer> lerArquivo(String arquivo) throws IOException {

		Map<String, Integer> lista = new LinkedHashMap<String, Integer>();

		// FileReader ler = new FileReader(arquivo);
		BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(arquivo), "ISO-8859-1"));
		String input;
		while ((input = reader.readLine()) != null) {

			// Cria StringTokenizer para a frase de entrada
			StringTokenizer frase = new StringTokenizer(input);

			while (frase.hasMoreTokens()) {
				String palavra = frase.nextToken().toLowerCase();

				palavra = removerAcentos(palavra);

				// Se o Hash tiver a palavra
				if (lista.containsKey(palavra)) {
					int contador = lista.get(palavra);
					lista.put(palavra, contador + 1);
				} else
					lista.put(palavra, 1);

			}

		}

		reader.close();

		return lista;
	}

	public static Map<String, Integer> lerConsulta(String consulta) throws IOException {

		Map<String, Integer> lista = new LinkedHashMap<String, Integer>();

		// Cria StringTokenizer para a frase de entrada
		StringTokenizer frase = new StringTokenizer(consulta);

		while (frase.hasMoreTokens()) {
			String palavra = frase.nextToken().toLowerCase();

			palavra = removerAcentos(palavra);

			// Se o Hash tiver a palavra
			if (lista.containsKey(palavra)) {
				int contador = lista.get(palavra);
				lista.put(palavra, contador + 1);
			} else
				lista.put(palavra, 1);

		}

		return lista;
	}

	public static void lerArquivos(List<String> arquivos) throws IOException {

		for (String arquivo : arquivos) {

			// FileReader ler = new FileReader(arquivo);
			BufferedReader reader = new BufferedReader(
					new InputStreamReader(new FileInputStream(arquivo), "ISO-8859-1"));
			String input;
			while ((input = reader.readLine()) != null) {

				// Cria StringTokenizer para a frase de entrada
				StringTokenizer frase = new StringTokenizer(input);

				// Processamento a frase de entrada
				while (frase.hasMoreTokens()) {
					String palavra = frase.nextToken().toLowerCase();

					palavra = removerAcentos(palavra);

					if (palavra.length() > 0) {

						// Se o Hash tiver a palavra
						if (vocabulario.containsKey(palavra)) {
							int contador = vocabulario.get(palavra);
							vocabulario.put(palavra, contador + 1);
						} else
							vocabulario.put(palavra, 1);

					}
				}

			}

			reader.close();
		}

	}

	public static List<Entry<String, Integer>> listarTermos() {
		Set<Entry<String, Integer>> set = vocabulario.entrySet();
		List<Entry<String, Integer>> list = new ArrayList<Entry<String, Integer>>(set);
		Collections.sort(list, new Comparator<Map.Entry<String, Integer>>() {
			public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
				return (o2.getValue()).compareTo(o1.getValue());
			}
		});

		return list;
	}

	public static List<Entry<String, Double>> ordenarDocumentos() {
		Set<Entry<String, Double>> set = similaridadeDocumentos.entrySet();
		List<Entry<String, Double>> list = new ArrayList<Entry<String, Double>>(set);
		Collections.sort(list, new Comparator<Map.Entry<String, Double>>() {
			public int compare(Map.Entry<String, Double> o1, Map.Entry<String, Double> o2) {
				return (o2.getValue()).compareTo(o1.getValue());
			}
		});

		return list;
	}

	public static void CalcularIdf(List<String> arquivos) throws IOException {

		for (int i = 0; i < listarTermos().size(); i++) {

			double qtdDocsComTermoVocabulario = 0.0;

			// Quantidade de documentos onde o termo aparece
			for (String arquivo : arquivos) {

				if (lerArquivo(arquivo).containsKey(listarTermos().get(i).getKey())) {
					qtdDocsComTermoVocabulario++;
				}

			}

			// Calculo do IDF
			if (qtdDocsComTermoVocabulario == 0) {
				valoresIdf.put(listarTermos().get(i).getKey(), 0.0);
			} else {
				valoresIdf.put(listarTermos().get(i).getKey(),
						(Math.log(arquivos.size() / qtdDocsComTermoVocabulario) / Math.log(2.0)));
			}
		}

	}

	public static void calcularTF(String arquivo) throws IOException {

		Map<String, Integer> tf = new LinkedHashMap<String, Integer>();

		valoresTf = new LinkedHashMap<String, Double>();
		tf = lerArquivo(arquivo);

		for (Map.Entry<String, Integer> entry : listarTermos()) {
			if (tf.containsKey(entry.getKey())) {
				Double valor = 1 + (Math.log(tf.get(entry.getKey())) / Math.log(2));
				valoresTf.put(entry.getKey(), valor);
			} else {
				valoresTf.put(entry.getKey(), 0.0);
			}
		}

	}

	public static void calcularTFConsulta(String consulta) throws IOException {

		Map<String, Integer> tf = new LinkedHashMap<String, Integer>();

		valoresTfConsulta = new LinkedHashMap<String, Double>();
		tf = lerConsulta(consulta);

		for (Map.Entry<String, Integer> entry : listarTermos()) {
			if (tf.containsKey(entry.getKey())) {
				Double valor = 1 + (Math.log(tf.get(entry.getKey())) / Math.log(2));
				valoresTfConsulta.put(entry.getKey(), valor);
			} else {
				valoresTfConsulta.put(entry.getKey(), 0.0);
			}
		}

	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static void calcularTfIdf() {

		List listaTf = new ArrayList(valoresTf.entrySet());
		List listaIdf = new ArrayList(valoresIdf.entrySet());

		valoresTfIdf = new LinkedHashMap<String, Double>();

		for (int i = 0; i < vocabulario.size(); i++) {
			Object valorTf = listaTf.get(i);
			String[] tf = valorTf.toString().split("=");

			Object valorIdf = listaIdf.get(i);
			String[] idf = valorIdf.toString().split("=");

			double tfIdf = Double.parseDouble(tf[1]) * Double.parseDouble(idf[1]);

			valoresTfIdf.put(tf[0], tfIdf);

		}

	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static void calcularTfIdfConsulta() {

		List listaTf = new ArrayList(valoresTfConsulta.entrySet());
		List listaIdf = new ArrayList(valoresIdf.entrySet());

		valoresTfIdfConsulta = new LinkedHashMap<String, Double>();

		for (int i = 0; i < vocabulario.size(); i++) {
			Object valorTf = listaTf.get(i);
			String[] tf = valorTf.toString().split("=");

			Object valorIdf = listaIdf.get(i);
			String[] idf = valorIdf.toString().split("=");

			double tfIdf = Double.parseDouble(tf[1]) * Double.parseDouble(idf[1]);

			valoresTfIdfConsulta.put(tf[0], tfIdf);

		}

	}

	public static double calcularNorma(Set<Entry<String, Double>> set) throws IOException {

		double norma = 0.0;

		for (Entry<String, Double> entry : set) {
			if (!entry.getValue().equals(0.0)) {
				norma = norma + Math.pow(entry.getValue(), 2.0);
			}
		}

		return Math.sqrt(norma);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static void similaridade(String nomeDocumento, double doc, double con) throws IOException {

		List listaTfIdf = new ArrayList(valoresTfIdf.entrySet());
		List listaTfIdfConsulta = new ArrayList(valoresTfIdfConsulta.entrySet());

		double resultado = 0.0;

		for (int i = 0; i < vocabulario.size(); i++) {
			Object valorTfIdf = listaTfIdf.get(i);
			String[] tfIdf = valorTfIdf.toString().split("=");

			Object valorTfIdfConsulta = listaTfIdfConsulta.get(i);
			String[] tfIdfConsulta = valorTfIdfConsulta.toString().split("=");

			resultado = resultado + (Double.parseDouble(tfIdf[1]) * Double.parseDouble(tfIdfConsulta[1]));

		}

		Double similaridade = new Double(resultado / (doc * con));

		if (similaridade.isNaN())
			similaridade = 0.0;

		similaridadeDocumentos.put(nomeDocumento, similaridade);

	}

	public static void limpar() {
		vocabulario = new LinkedHashMap<String, Integer>();
		valoresTf = new LinkedHashMap<String, Double>();
		valoresIdf = new LinkedHashMap<String, Double>();
		valoresTfIdf = new LinkedHashMap<String, Double>();
		valoresTfConsulta = new LinkedHashMap<String, Double>();
		valoresTfIdfConsulta = new LinkedHashMap<String, Double>();
		similaridadeDocumentos = new LinkedHashMap<String, Double>();
	}

	public static void toDo() {

		String[] nomesDocumentos = { "documentos/d1.txt", "documentos/d2.txt", "documentos/d3.txt",
				"documentos/d4.txt" };

		String[] titulos = { "Documento 1", "Documento 2", "Documento 3", "Documento 4" };

		List<String> documentos = Arrays.asList(nomesDocumentos);

		List<String> titulosDocumentos = Arrays.asList(titulos);

		limpar();

		scanner = new Scanner(System.in);

		try {

			lerArquivos(documentos);

			CalcularIdf(documentos);

			System.out.println("Digite a Consulta: ");
			String consulta = scanner.nextLine();

			calcularTFConsulta(consulta);

			for (int i = 0; i < documentos.size(); i++) {
				calcularTF(documentos.get(i));
				calcularTfIdf();

				calcularTfIdfConsulta();
				similaridade(titulosDocumentos.get(i), calcularNorma(valoresTfIdf.entrySet()),
						calcularNorma(valoresTfIdfConsulta.entrySet()));
			}

			System.out.println("\nGrau de Similaridade entre Consulta e Documentos: \n");
			for (Map.Entry<String, Double> entry : ordenarDocumentos()) {
				System.out.println(entry.getKey() + " " + entry.getValue().floatValue());
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void hinos() {

		String[] nomesDocumentos = { "documentos/HinoNacional.txt", "documentos/HinoBandeira.txt",
				"documentos/HinoIndependencia.txt" };

		String[] tituloDocumentos = { "Hino Nacional", "Hino Bandeira", "Hino Independencia" };

		List<String> documentos = Arrays.asList(nomesDocumentos);

		List<String> titulosDocumentos = Arrays.asList(tituloDocumentos);

		limpar();

		scanner = new Scanner(System.in);

		try {

			lerArquivos(documentos);

			CalcularIdf(documentos);

			System.out.println("Digite a Consulta: ");
			String consulta = scanner.nextLine();

			calcularTFConsulta(consulta);

			for (int i = 0; i < documentos.size(); i++) {
				calcularTF(documentos.get(i));
				calcularTfIdf();

				calcularTfIdfConsulta();
				similaridade(titulosDocumentos.get(i), calcularNorma(valoresTfIdf.entrySet()),
						calcularNorma(valoresTfIdfConsulta.entrySet()));
			}

			System.out.println("\nGrau de Similaridade entre Consulta e Documentos: \n");
			for (Map.Entry<String, Double> entry : ordenarDocumentos()) {
				System.out.println(entry.getKey() + " " + entry.getValue().floatValue());
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) throws IOException {

		toDo();

	}
}
