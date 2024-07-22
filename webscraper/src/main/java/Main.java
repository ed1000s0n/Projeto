import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Scanner;
import java.io.FileWriter;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class Main {
    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);

        System.out.print("Digite ou cole o link do produto desejado: ");
        String userLink = scanner.nextLine();
        String requiredPrefix = "https://www.netshoes.com.br/p/";

        if (isValidURL(userLink) && hasRequiredPrefix(userLink, requiredPrefix)) {
            if (isURLAccessible(userLink)) {
                System.out.println("O link é válido, tem o prefixo necessário e é acessível.");
                try {
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
    }

    public static void generateJSONFile(String userLink) throws IOException {
        try (final WebClient webClient = new WebClient(BrowserVersion.CHROME)) {
            webClient.getOptions().setJavaScriptEnabled(false);
            webClient.getOptions().setCssEnabled(false);

            HtmlPage page = webClient.getPage(userLink);
            webClient.waitForBackgroundJavaScript(10000);
            String pageAsXml = page.asXml();
            Document doc = Jsoup.parse(pageAsXml);

            Elements scripts = doc.select("script[type=application/ld+json]");
            JSONObject productObject = null;
            productObject = TratamentoErro404(scripts, productObject);

            if (productObject != null) {
                String name = productObject.optString("name");
                String description = productObject.optString("description");
                JSONArray imageArray = productObject.optJSONArray("image");
                String lowPrice = productObject.optJSONObject("offers").optString("lowPrice");

                JSONObject jsonOutput = new JSONObject();
                jsonOutput.put("name", name);
                jsonOutput.put("description", description);
                jsonOutput.put("lowPrice", lowPrice);

                ArrayList<String> images = new ArrayList<String>();
                if (imageArray != null) {
                    for (Object imagem : imageArray) {
                        images.add("https://static.netshoes.com.br" + imagem.toString());
                    }
                }
                Produto dados = new Produto(name, lowPrice, description, images);
                Imprimir(dados);
                TelaOutput saida = new TelaOutput();
                saida.AtualizarDados(dados);
                jsonOutput.put("images", imageArray);

                try (FileWriter file = new FileWriter("ArquivoJson/product_info.json")) {
                    file.write(jsonOutput.toString(4));
                    System.out.println("Arquivo JSON gerado com sucesso!");
                    System.exit(0);
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

    public static void Imprimir(Produto p) {
        System.out.println("Nome do Produto: " + p.getNome() + "\n");
        System.out.println("Preço: " + p.getPreco() + "\n");
        System.out.println("Descrição do produto: " + p.getDescricao() + "\n");

        if (p.getImagem() != null) {
            for (String imagem : p.getImagem()) {
                System.out.println("Imagem: " + imagem);
            }
        }

    }

    public static JSONObject TratamentoErro404(Elements scripts, JSONObject productObject) {

        for (Element script : scripts) {
            String jsonLd = script.html();
            jsonLd = jsonLd.replaceAll("//<!\\[CDATA\\[", "").replaceAll("//\\]\\]>", "").trim();
            JSONObject jsonObject = new JSONObject(jsonLd);
            JSONArray graphArray = null;
            if (jsonObject.has("@graph")) {
                graphArray = jsonObject.getJSONArray("@graph");

            } else {
                System.out.println("Erro 404 encontrado, sua URL não é valida ou o produto está indispinivel.");
                System.exit(0);
            }
            productObject = graphArray.getJSONObject(0);
        }
        return productObject;

    }
}