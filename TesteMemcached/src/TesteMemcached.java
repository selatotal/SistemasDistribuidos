import java.io.IOException;
import java.util.concurrent.TimeoutException;

import com.google.gson.Gson;

import net.rubyeye.xmemcached.MemcachedClient;
import net.rubyeye.xmemcached.XMemcachedClient;
import net.rubyeye.xmemcached.XMemcachedClientBuilder;
import net.rubyeye.xmemcached.exception.MemcachedException;
import net.rubyeye.xmemcached.utils.AddrUtil;

public class TesteMemcached {

	public static void main(String[] args) {

		// Inicializa uma conexao com o Memcached
		XMemcachedClientBuilder builder = new XMemcachedClientBuilder(AddrUtil.getAddresses("localhost:11211"));
		try {
			// Cria um cliente Memcached
			MemcachedClient client = builder.build();

			String nome = client.get("chaveNome");
			if (nome == null){
				System.out.println("Chave nao encontrada no cache. Adicionando...");
				client.set("chaveNome", 0, "Tales Viegas");
			} else {
				System.out.println("Chave encontrada no cache: " + nome);
				System.out.println("Vou atualizar a chave!");
				client.set("chaveNome", 0, "John Doe");
			}
			
			// Exemplo JSON
			Gson gson = new Gson();
			String json = "{ "+ 
					"    \"servers\": [" + 
					"        {" + 
					"            \"name\": \"serverTales\"," + 
					"            \"location\": \"999.999.999.999:9999\"," + 
					"            \"sectors\": [" + 
					"                {" + 
					"                	\"code\" : 1," + 
					"                	\"description\" : \"setor 1\"" + 
					"                }," + 
					"                {" + 
					"                	\"code\" : 2," + 
					"                	\"description\" : \"setor 2\"" + 
					"                }," + 
					"                {" + 
					"                	\"code\" : 3," + 
					"                	\"description\" : \"setor 3\"" + 
					"                }" + 
					"            ]," + 
					"            \"active\": true" + 
					"        }," + 
					"        {" + 
					"            \"name\": \"serverViegas\"," + 
					"            \"location\": \"999.999.999.999:9999\"," + 
					"            \"sectors\": [" + 
					"                {" + 
					"                	\"code\" : 1," + 
					"                	\"description\" : \"setor 1\"" + 
					"                }," + 
					"                {" + 
					"                	\"code\" : 2," + 
					"                	\"description\" : \"setor 2\"" + 
					"                }," + 
					"                {" + 
					"                	\"code\" : 3," + 
					"                	\"description\" : \"setor 3\"" + 
					"                }" + 
					"            ]," + 
					"			\"active\": false" + 
					"        }" + 
					"    ]" + 
					"}";
			
			// Converte um JSON para um objeto (no caso um ListServersModel)
			ListServersModel model = gson.fromJson(json, ListServersModel.class);
			
			// Converte um Objeto para um JSON
			json = gson.toJson(model);
			
			// adiciona o JSON no Memcached
			client.set("SD_ListServers", 0, gson.toJson(model));
			
			// Busca o JSON do Memcached
			json = client.get("SD_ListServers");
			model = gson.fromJson(json, ListServersModel.class);
			
			// Atualiza o objeto
			model.servers.get(0).active = !model.servers.get(0).active;
			
			// Salva novamente no Memcached
			json = gson.toJson(model);
			client.set("SD_ListServers", 0, gson.toJson(model));
			
			client.shutdown();
					
		} catch (IOException e) {
			e.printStackTrace();
		} catch (TimeoutException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (MemcachedException e) {
			e.printStackTrace();
		}
		
		
		

	}

}
