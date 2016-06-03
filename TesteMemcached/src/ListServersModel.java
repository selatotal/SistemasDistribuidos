import java.io.Serializable;
import java.util.List;

public class ListServersModel implements Serializable{

	List<ServerModel> servers;
		
	public List<ServerModel> getServers() {
		return servers;
	}

	public void setServers(List<ServerModel> servers) {
		this.servers = servers;
	}

	
}
