import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.Scanner;
import java.io.FileWriter;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;

import java.net.URL;

public class Main {
    public static void main(String[] args) {
        // Exemplo de link recebido do usuário
        Scanner scanner = new Scanner(System.in);

        // Lê uma linha de texto
        System.out.print("Digite ou cole o link do produto desejado: ");
        String userLink = scanner.nextLine();
        //String userLink = "https://www.netshoes.com.br/p/capacete-bike-ciclismo-abus-gamechanger-oem-aerodinamico-branco-CYY-0242-014";
        String requiredPrefix = "https://www.netshoes.com.br/p/";

        if (isValidURL(userLink) && hasRequiredPrefix(userLink, requiredPrefix)) {
            if (isURLAccessible(userLink)) {
                System.out.println("O link é válido, tem o prefixo necessário e é acessível.");
                try {
                    // Chama o método para gerar o arquivo JSON
                    generateJSONFile(userLink);
                } catch (IOException e) {
                    System.out.println("Erro ao gerar o arquivo JSON.");
                    e.printStackTrace();
                }
            } else {
                System.out.println("O link tem o prefixo necessário, mas não é acessível.");
            }
        } else {
            System.out.println("O link não é válido ou não tem o prefixo necessário.");
        }

        try (final WebClient webClient = new WebClient(BrowserVersion.CHROME)) {

            webClient.getOptions().setJavaScriptEnabled(false); // Desativa JavaScript
            webClient.getOptions().setCssEnabled(false); // Desativa CSS se não for necessário

            // Carregar a página
            HtmlPage page = webClient.getPage(userLink);

            // Esperar a página carregar completamente
            webClient.waitForBackgroundJavaScript(10000); // Aumente o tempo se necessário

            // Obter o HTML como String
            String pageAsXml = page.asXml();

            // Usar Jsoup para parsear o HTML
            Document doc = Jsoup.parse(pageAsXml);

            // Encontrar a tag <script> com o tipo application/ld+json
            Elements scripts = doc.select("script[type=application/ld+json]");
            for (Element script : scripts) {
                // Extrair o conteúdo do script, removendo CDATA
                String jsonLd = script.html();
                jsonLd = jsonLd.replaceAll("//<!\\[CDATA\\[", "").replaceAll("//\\]\\]>", "").trim();

                // Parsear o JSON-LD
                JSONObject jsonObject = new JSONObject(jsonLd);
                JSONArray graphArray = jsonObject.getJSONArray("@graph");

                // Encontrar o objeto Product no array
                JSONObject productObject = graphArray.getJSONObject(0);

                // Extrair as informações desejadas
                String name = productObject.optString("name");
                String description = productObject.optString("description");
                JSONArray imageArray = productObject.optJSONArray("image");
                String lowPrice = productObject.optJSONObject("offers").optString("lowPrice");

                // Imprimir as informações
                System.out.println("Name: " + name);
                System.out.println("Low Price: " + lowPrice);
                System.out.println("description: " + description);

                if (imageArray != null) {
                    for (int i = 0; i < imageArray.length(); i++) {
                        System.out.println("Imagem:https://static.netshoes.com.br" + imageArray.getString(i));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void generateJSONFile(String userLink) throws IOException {
        try (final WebClient webClient = new WebClient(BrowserVersion.CHROME)) {
            webClient.getOptions().setJavaScriptEnabled(false); // Desativa JavaScript
            webClient.getOptions().setCssEnabled(false); // Desativa CSS se não for necessário

            // Carregar a página
            HtmlPage page = webClient.getPage(userLink);

            // Esperar a página carregar completamente
            webClient.waitForBackgroundJavaScript(10000); // Aumente o tempo se necessário

            // Obter o HTML como String
            String pageAsXml = page.asXml();

            // Usar Jsoup para parsear o HTML
            Document doc = Jsoup.parse(pageAsXml);

            // Encontrar a tag <script> com o tipo application/ld+json
            Elements scripts = doc.select("script[type=application/ld+json]");
            JSONObject productObject = null;
            for (Element script : scripts) {
                // Extrair o conteúdo do script, removendo CDATA
                String jsonLd = script.html();
                jsonLd = jsonLd.replaceAll("//<!\\[CDATA\\[", "").replaceAll("//\\]\\]>", "").trim();

                // Parsear o JSON-LD
                JSONObject jsonObject = new JSONObject(jsonLd);
                
                if (jsonObject.has("@graph")) {
                    // O campo existe, você pode acessá-lo
                    JSONObject graph = jsonObject.getJSONObject("@graph");
                    System.out.println("Nome: " + graph.getString("name"));
                    System.out.println("Idade: " + graph.getInt("age"));
                } else {
                    // O campo não existe
                    System.out.println("Campo '@graph' não encontrado.");
                    System.exit(0);
                }
                JSONArray graphArray = jsonObject.getJSONArray("@graph");
                // Encontrar o objeto Product no array
                productObject = graphArray.getJSONObject(0);
            }

            if (productObject != null) {
                // Extrair as informações desejadas
                String name = productObject.optString("name");
                String description = productObject.optString("description");
                JSONArray imageArray = productObject.optJSONArray("image");
                String lowPrice = productObject.optJSONObject("offers").optString("lowPrice");

                // Criar o objeto JSON final
                JSONObject jsonOutput = new JSONObject();
                jsonOutput.put("name", name);
                jsonOutput.put("description", description);
                jsonOutput.put("lowPrice", lowPrice);

                JSONArray images = new JSONArray();
                if (imageArray != null) {
                    for (int i = 0; i < imageArray.length(); i++) {
                        images.put("https://static.netshoes.com.br" + imageArray.getString(i));
                    }
                }
                jsonOutput.put("images", images);

                // Salvar o JSON em um arquivo
                try (FileWriter file = new FileWriter("ArquivoJson/product_info.json")) {
                    file.write(jsonOutput.toString(4)); // 4 é o nível de indentação
                    System.out.println("Arquivo JSON gerado com sucesso!");
                }
            }
        } catch (Exception e) {
            throw new IOException("Erro ao gerar o arquivo JSON.", e);
        }
    }
    public static boolean isValidURL(String url) {
        try {
            new URL(url);
            return true;
        } catch (MalformedURLException e) {
            return false;
        }
    }

    public static boolean hasRequiredPrefix(String url, String prefix) {
        return url.startsWith(prefix);
    }

    public static boolean isURLAccessible(String url) {
        try {
            HttpURLConnection huc = (HttpURLConnection) new URL(url).openConnection();
            huc.setRequestMethod("HEAD");
            int responseCode = huc.getResponseCode();
            return (responseCode == HttpURLConnection.HTTP_OK);
        } catch (IOException e) {
            return false;
        }
    }
}