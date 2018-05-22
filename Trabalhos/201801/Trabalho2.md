# Dados de entrega
* Data: 26/06/2016
* Peso: 10 pontos da nota de G2
* Entregar os arquivos-fonte de todos os componentes criados, da aplicação cliente e servidor e os binários das bibliotecas utilizadas (caso o download e configuração não esteja dentro do script de build). Para evitar problemas, o melhor é fazer um ZIP de toda a pasta do projeto ou subir o projeto no Github e enviar o link.
* Entregar os scripts de criação de base de dados (caso utilize)
* Criar um manual de instruções de como configurar o ambiente para testes. Caso o professor não consiga configurar o ambiente utilizando apenas as instruções do roteiro de testes, o trabalho terá nota 0 (zero)

# Definição

O aluno deverá implementar um sistema distribuído para que seja possível visualizar dados sobre performance de times e jogadores de futebol europeu.
O aluno deverá implementar tanto um cliente como um processo servidor que será uma parte do sistema distribuído.

# Origem dos dados

Arquivos contendo todos os detalhes de partidas mais de 25 mil partidas de futebol realizadas no continente europeu entre os anos de 2008 e 2016. Importante: as temporadas de futebol no continente europeu iniciam no meio do ano e se extendem até o próximo (ex: 2010/2011). Para este trabalho, usaremos como referência de ano a data da partida.
Esta base de dados possui o tamanho de 300Mb, e está organizada em um arquivo sqlite. O aluno pode utilizar o próprio SQLite para o trabalho ou converter para uma base de dados de sua preferência.
Os dados e a definição de cada um dos campos do arquivo estão em https://www.kaggle.com/hugomathien/soccer

Cada um dos programas servidores (ou seja, cada grupo de aluno) será responsável por informações de um determinado ano. Entretanto, devem trabalhar com a base de dados inteira, sendo o ano pelo qual o grupo é responsável configurável via arquivo de configuração

# Informações a serem fornecidas

* Número de jogos
* Número de vitórias
* Número de derrotas

# Filtros possíveis

Deverá ser possível qualquer combinação dos seguintes filtros. Apenas o filtro de período é obrigatório:
* Período: Ano, Ano/Mês
* Time
* Jogador

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

# Arquivo de configuração do servidor

O processo servidor deve possuir um arquivo de configuração com o nome config.json, conforme definido abaixo:

```json
{
  "serverName" : "serverTales",
  "serverIP" : "127.0.0.1",
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

## Chave: SD_ListServers
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

# Chaves de Busca de Dados
  - SD_Data_&lt;periodo&gt;
  - SD_Data_&lt;periodo&gt;_&lt;clube&gt;
  - SD_Data_&lt;periodo&gt;__&lt;jogador&gt;
  - SD_Data_&lt;periodo&gt; _ &lt;clube&gt; _ &lt;jogador&gt;
* Objetivo: Conter os dados de retorno por período, período/clube, período/jogador, período/clube/jogador
* O período deve estar no formato YYYY (ano) ou YYYYMM (ano/mês)
* Operações:
  - Buscar os dados
  - Inserir os dados
* Formato: 
```json
{
    "wins": 123,
    "losses": 456,
}
```

# O cliente do usuário
O cliente deve conectar a um dos servidores disponíveis. Qualquer servidor deve ser apto a receber requisições de qualquer cliente.
Assim como no trabalho de G1, o cliente deve informar IP/Porta do servidor que ele quer conectar.
A partir da conexão, o cliente deve listar todos os anos disponíveis para consulta e campos para pesquisa de dados.
Ao efetuar a busca, o cliente deve enviar a requisição ao servidor ao qual ele está conectado e exibir os dados retornados ou a informação de que nenhum dado foi encontrado.

# Protocolo de Comunicação entre Cliente e Servidor

Toda a comunicação entre cliente e servidor deve ser feita via HTTP, utilizando os endpoints abaixo e retornando a informação em JSON

## Busca de Anos Disponíveis
* Retorna a lista com todos os anos disponíveis para consulta dentro do sistema distribuídoindependente de qual processo é responsável por cada ano.

### Requisição
```
/getAvailabeYears
```
### Resposta
```json
{
    "years": [ 1999, 2000, 2001, 2002]
}
```

## Busca dos dados
* Busca dos dados por período, período/aeroporto ou período/aeroporto/companhia aérea

### Requisição
```
/getData/<período>?playerName=<nomeDoJogador>
/getData/<período>?clubName=<nomeDoClube>
/getData/<período>?clubName=<nomeDoClube>&playerName=<nomeDoJogador>

onde: 
  - período que deseja pesquisar em um dos seguintes formatos (obrigatório):
    - YYYY -> (ex: 2015) Ano. Mostra todos os gastos durante o ano especificado
    - YYYYMM -> (ex: 201501) Mês. Mostra todos os gastos durante o ano/mês especificado
    - YYYYMMDD -> (ex: 20150101) Dia. Mostra todos os gastos durante o ano/mês/dia especificado
  - playerName - Nome do jogador a ser pesquisado
  - clubName - Nome do clube a ser pesquisado

Exemplos:
/getData/2010?playerName=Lionel+Messi
/getData/2010?clubName=Real+Madrid
/getData/2010?clubName=Barcelona&playerName=Neymar+Jr
```

### Resposta
```json
{
    "wins": 123,
    "losses": 456,
}
```

## Erros

No caso de alguma requisição ocorrer erro, deve ser retornado retornado o código HTTP 417 com um JSON no seguinte formato:
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
