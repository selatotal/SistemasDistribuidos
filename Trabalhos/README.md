
# Definição da lista de Chaves a serem armazenadas 

##Chave: SD_ListServers
* Objetivo: Buscar a lista de servidores ativos do sistema distribuído e de quais órgãos ele possui informação
* Operações:
  - Buscar a lista de servidores ativos
  - Atualizar a lista para que o processo possa ser adicionado à lista e/ou alterada a sua situação
  - Atualizar a lista para que, caso a comunicação com um dos servidores falhe, alterar a situação para falso
* Formato:
```json
{
    "servers": [
        {
            "name": "serverTales",
            "location": "999.999.999.999:9999",
            "sectors": [
                {
                	"codigo" : 1,
                	"descricao" : "orgao 1"
                },
                {
                	"codigo" : 2,
                	"descricao" : "orgao 2"
                },
                {
                	"codigo" : 3,
                	"descricao" : "orgao 3"
                }
            ],
            "active": true
        },
        {
            "name": "serverViegas",
            "location": "999.999.999.999:9999",
            "sectors": [
                {
                	"codigo" : 1,
                	"descricao" : "orgao 1"
                },
                {
                	"codigo" : 2,
                	"descricao" : "orgao 2"
                },
                {
                	"codigo" : 3,
                	"descricao" : "orgao 3"
                }
            ],
			"active": false
        }
    ]
}
```

## Chave: SD_&lt;servidor&gt;_totalReq
* Objetivo: Conter o total de requisições a um servidor
* Operações:
  - Buscar o total de requisições realizadas a um servidor
  - O servidor deve incrementar a quantidade de requisições a cada requisição recebida
* Formato: 
```
9999999999
```

# Chaves de Gastos de Órgão
  - SD_GastoOrgao_&lt;codigoOrgao&gt;_&lt;YYYY&gt;
  - SD_GastoOrgao_&lt;codigoOrgao&gt;_&lt;YYYYMM&gt;
  - SD_GastoOrgao_&lt;codigoOrgao&gt;_&lt;YYYYMMDD&gt;
* Objetivo: Conter o total de gastos de um órgão por ano (YYYY), mês (YYYYMM) ou dia (YYYYMMDD)
* Operações:
  - Buscar o gasto do órgão
  - Inserir o gasto do órgão
* Formato: 