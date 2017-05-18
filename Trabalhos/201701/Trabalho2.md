# Dados de entrega
* Data: 06/07/2016
* Peso: 10 pontos da nota de G2
* Entregar os arquivos-fonte de todos os componentes criados, da aplicação cliente e servidor e os binários das bibliotecas utilizadas (caso o download e configuração não esteja dentro do script de build). Para evitar problemas, o melhor é fazer um ZIP de toda a pasta do projeto ou subir o projeto no Github e enviar o link.
* Entregar os scripts de criação de base de dados (caso utilize)
* Criar um manual de instruções de como configurar o ambiente para testes. Caso o professor não consiga configurar o ambiente utilizando apenas as instruções do roteiro de testes, o trabalho terá nota 0 (zero)

# Definição

O aluno deverá implementar um sistema distribuído para que seja possível visualizar dados anuais sobre atrasos de vôo relativos ao transporte aéreo realizado em aeroportos norte-americanos.
O aluno deverá implementar tanto um cliente como um processo servidor que será uma parte do sistema distribuído.

# Origem dos dados

Arquivos contendo todos os detalhes de partidas e chegadas de todos os vôos comerciais nos Estados Unidos de Outubro de 1987 a Abril de 2008. 
Estes arquivos possuem cerca de 120 milhões de registros no total, e possuem 1.6Gb comprimidos (cerca de 12Gb expandidos).
Os dados e a definição de cada um dos campos do arquivo estão em http://stat-computing.org/dataexpo/2009/the-data.html
Arquivos complementares como a lista das siglas de aeroportos, lista de companhias aéreas e modelos de aviẽs podem ser obtidos em http://stat-computing.org/dataexpo/2009/supplemental-data.html

Cada um dos programas servidores (ou seja, cada grupo de aluno) será responsável por informações de um determinado ano. Como os arquivos de origem são divididos por ano, faremos uma divisão destes arquivos por grupo.

# Informações a serem fornecidas

* Quantidade de vôos com partida no horário
* Quantidade de vôos com partida em atraso
* Quantidade de vôos com chegada no horário
* Quantidade de vôos com chegada em atraso
* Tempo médio de atrasos

# Filtros possíveis

Deverá ser possível qualquer combinação dos seguintes filtros. Apenas o filtro de período é obrigatório:
* Período: Ano, Ano/Mês ou Ano/Mês/Dia
* Aeroporto
* Companhia Aérea

# Sincronização e Localização

## Memcached
Para fins de sincronização/localização de qual servidor é responsável por qual ano, será utilizado um servidor Memcached (http://memcached.org).
O memcached é um servidor de memória distribuída que possibilita o armazenamento de informações através de chave/valor. Apesar de existirem versões para Windows, os desenvolvedores afirmam que o suporte é apenas para servidores Linux/BSD-like.
O Sistema Distribuído poderá ter 1 ou mais servidores de memcached ativos. Para a realização deste trabalho você pode assumir que teremos apenas 1 servidor em todo o sistema distribuído.
Todas as chaves serão no formato texto e todos os valores em formato JSON.

## Responsabilidades dos Processos

* Cada processo é responsável por incluir/atualizar as chaves no servidor Memcached. Caso o valor de uma chave não exista, ela deve ser criada pelo processo.
* IMPORTANTE: Não utilizar o comando "replace" do Memcached. Para atualizar uma chave realize o "get" dela, atualize o valor e depois faça o "set".
* O processo cliente NÃO DEVE armazenar informação alguma no Memcached. Apenas o processo servidor deve armazenar dados no Memcached.
* O processo cliente não deve realizar requisições para servidores que não estejam ativos. Além disso, caso tente realizar uma requisição para um servidor e verifique que ele não está mais respondendo, ele deve alterar o status do servidor para inativo (active=false) e atualizar a lista de servidores.

# Arquivo de configuração do servidor

O processo servidor deve possuir um arquivo de configuração com o nome config.json, conforme definido abaixo:

```json
{
  "serverName" : "serverTales",
  "portListen" : 1111,
  "memcachedServer" : "10.1.1.1",
  "memcachedPort" : 11211,
  "yearData" : [1999, 2000]
}

```

Onde:
* serverName = Nome idenficador do servidor. Deve ser único em todo o sistema distribuído
* portListen = Número da porta TCP que o servidor deve receber requisições dos clientes
* memcachedServer = IP onde o servidor de memcached está disponível
* memcachedPort = Porta que o servidor de memcached está ouvindo as requisições
* yearData = Ano o qual o servidor contém dados (um servidor pode ser responsável por mais de um ano)

São permitidas configurações adicionais, desde que combinadas com o professor anteriormente.

# Definição da lista de Chaves a serem armazenadas 

##Chave: SD_ListServers
* Objetivo: Buscar a lista de servidores ativos do sistema distribuído e de quais anos ele possui informação
* Esta chave deve ser atualizada pelos servidores a cada n segundos (especificada no arquivo de configuração abaixo) e, caso tenha sido alterada, o processo deve refazer a sua configuração da lista de servidores de acordo com o novo resultado.
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
            "year": [1999, 2000],
            "active": true
        },
        {
            "name": "serverViegas",
            "location": "999.999.999.999:9999",
            "year": [2000],
			"active": false
        }
    ]
}
```

# Lista de Aeroportos: SD_Airports
* Objetivo: Lista de todos os aeroportos disponíveis
* Operações: 
  - Buscar a lista de aeroportos
  - Inserir a lista de aeroportos
* Formato:
```json
{
    "airports": [
        {
            "iata": "POA",
            "name": "Aeroporto Internacional Salgado Filho",
            "city": "Porto Alegre",
            "lat": -30.0277,
            "long": -51.2287
        },
        {
            "iata": "FLN",
            "name": "Aeroporto Internacional Hercilio Luz",
            "city": "Florianopolis",
            "lat": -27.6701,
            "long": -48.546
        }
    ]
}
```

# Lista de Companhias Aéreas: SD_Carriers
* Objetivo: Listar as companhias aéreas disponíveis
* Operações: 
  - Buscar a lista de companhias aéreas
  - Inserir a lista de companhias aéreas
```json
{
    "carriers": [
        {
            "code": "AA",
            "name": "American Airlines"
        },
        {
            "iata": "AR",
            "name": "Aerolineas Argentinas"
        }
    ]
}
```

# Chaves de Busca de Dados
  - SD_Data_&lt;periodo&gt;
  - SD_Data_&lt;periodo&gt;_&lt;aeroporto&gt;
  - SD_Data_&lt;periodo&gt;__&lt;companhia aérea&gt;
  - SD_Data_&lt;periodo&gt;_&lt;aeroporto&gt;_&lt;companhia aérea&gt;
* Objetivo: Conter os dados de retorno por período, período/aeroporto, período/aeroporto/companhia aérea
* O período deve estar no formato YYYY (ano), YYYYMM (ano/mês) ou YYYYMMDD (ano/mês/dia)
* Operações:
  - Buscar os dados
  - Inserir os dados
* Formato: 
```json
{
    "arrivalOnTimeFlights": 123,
    "arrivalDelayedFlights": 456,
    "arrivalDelayedAverageTime": "00:00:12",
    "departureOnTimeFlights": 789,
    "departureDelayedFlights": 12,
    "departureDelayedAverageTime": "00:00:12"
}
```

# O cliente do usuário
O cliente deve conectar a um dos servidores disponíveis. Qualquer servidor deve ser apto a receber requisições de qualquer cliente.
Assim como no trabalho de G1, o cliente deve informar IP/Porta do servidor que ele quer conectar.
A partir da conexão, o cliente deve listar todos os anos disponíveis para consulta e campos para pesquisa de dados.
Ao efetuar a busca, o cliente deve enviar a requisição ao servidor ao qual ele está conectado e exibir os dados de atraso retornados ou a informação de que nenhum gasto foi encontrado.

# Protocolo de Comunicação entre Cliente e Servidor

Este protocolo deve ser seguido pelos clientes do servidor (seja ele o cliente do usuário ou outro servidor)

## Busca de Anos Disponíveis
* Retorna a lista com todos os anos disponíveis para consulta dentro do sistema distribuídoindependente de qual processo é responsável por cada ano.

### Requisição
```
GETAVAILABLEYEARS
```
### Resposta
```json
{
    "years": [ 1999, 2000, 2001, 2002]
}
```

## Busca de Aeroportos
* Retorna a lista com todos os aeroportos disponíveis.

### Requisição
```
GETAIRPORTS
```
### Resposta
```json
{
    "airports": [
        {
            "iata": "POA",
            "name": "Aeroporto Internacional Salgado Filho",
            "city": "Porto Alegre",
            "lat": -30.0277,
            "long": -51.2287
        },
        {
            "iata": "FLN",
            "name": "Aeroporto Internacional Hercilio Luz",
            "city": "Florianopolis",
            "lat": -27.6701,
            "long": -48.546
        }
    ]
}
```

## Busca de Companhias Aéreas
* Retorna a lista com todos as companhias aéreas disponíveis.

### Requisição
```
GETCARRIERS
```
### Resposta
```json
{
    "carriers": [
        {
            "code": "AA",
            "name": "American Airlines"
        },
        {
            "iata": "AR",
            "name": "Aerolineas Argentinas"
        }
    ]
}
```

## Busca dos dados
* Busca dos dados por período, período/aeroporto ou período/aeroporto/companhia aérea

### Requisição
```
GETDELAYDATA <período> <aeroporto> <companhia aérea>

onde: 
  - período que deseja pesquisar em um dos seguintes formatos (obrigatório):
    - YYYY -> (ex: 2015) Ano. Mostra todos os gastos durante o ano especificado
    - YYYYMM -> (ex: 201501) Mês. Mostra todos os gastos durante o ano/mês especificado
    - YYYYMMDD -> (ex: 20150101) Dia. Mostra todos os gastos durante o ano/mês/dia especificado
  - aeroporto - (opcional) código IATA do aeroporto. No caso do aeroporto não ter sido selecionado e a consulta for por companhia aérea, deve ser enviado o valor ***
  - companhia aérea - (opcional) código da companhia aérea

Exemplos:
GETDELAYDATA 1999
GETDELAYDATA 199907
GETDELAYDATA 19990715
GETDELAYDATA 1999 SFO
GETDELAYDATA 199907 SFO
GETDELAYDATA 19990715 SFO
GETDELAYDATA 1999 SFO LAN
GETDELAYDATA 199907 SFO LAN
GETDELAYDATA 19990715 SFO LAN
GETDELAYDATA 1999 *** LAN
GETDELAYDATA 199907 *** LAN
GETDELAYDATA 19990715 *** LAN
```

### Resposta
```json
{
    "arrivalOnTimeFlights": 123,
    "arrivalDelayedFlights": 456,
    "arrivalDelayedAverageTime": "00:00:12",
    "departureOnTimeFlights": 789,
    "departureDelayedFlights": 12,
    "departureDelayedAverageTime": "00:00:12"
}
```

## Erros

No caso de alguma requisição ocorrer erro, deve ser retornado um JSON no seguinte formato:
```json
{
    "errorCode": 123,
    "errorDescription": "Descricao do erro"
}
```


Código  | Descrição              | Usado quando
------- | ---------------------- | ------------
1       | Servidor Indisponível  | Quando o servidor que possui o dado está indisponívei
2       | Dados Inexistentes     | Quando os dados para a busca desejada não existem
