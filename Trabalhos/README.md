# Definição

O aluno deverá implementar um sistema distribuído para que seja possível visualizar os gastos por setor e/ou órgão do Governo do Estado do RS em 2015.
O aluno deverá implementar tanto um cliente como um processo servidor que será uma parte do sistema distribuído.

# Origem dos dados
Arquivo de gastos efetuados em 2015 pelo Governo do Estado do RS disponibilizados no Portal da Transparência: http://www.transparencia.rs.gov.br/ARQUIVOS/Gasto-RS-2015.zip. Para ver as primeiras 10.000 linhas do arquivo clique [aqui](Primeiras10000.csv)
* Este arquivo ZIP contém um arquivo .CSV de aproximadamente 3GB, contendo o registro de 3.791.594 gastos efetuados.
* O layout do arquivo CSV pode ser verificado no arquivo [LayoutGastos.pdf](LayoutGastos.pdf)

# Definição
Cada um dos grupos será responsável por determinados setores de gastos. Estes setores devem ser configuráveis na aplicação via um arquivo texto de configuração.
A quantidade mínima de registros esperados que cada servidor deverá tratar é de 1.000.000 de registros.
A saber, os setores e a quantidade de registros por setor pode ser verificada na lista abaixo:

```
712133 ENCARGOS FINANCEIROS DO ESTADO
541102 SECRETARIA ESTADUAL DA SAUDE
540202 SECRETARIA DA EDUCACAO
364898 SECRETARIA DA SEGURANCA PUBLICA
257401 FUNDO DE ASSISTENCIA A SAUDE - FAS/RS
197803 DEPARTAMENTO AUTONOMO DE ESTRADAS DE RODAGEM
167289 TRIBUNAL DE JUSTICA
157134 SECRETARIA DA FAZENDA
131016 DEPARTAMENTO ESTADUAL DE TRANSITO
82299 REGIME PROPRIO DE PREVIDENCIA SOCIAL DO ESTADO DO RIO GRANDE DO SUL - RPPS/RS
67439 MINISTERIO PUBLICO
43605 SECRETARIA DA AGRICULTURA E PECUARIA
40927 ASSEMBLEIA LEGISLATIVA
38395 INSTITUTO RIOGRANDENSE DO ARROZ
36977 FUNDACAO DE ATENDIMENTO SOCIO-EDUCATIVO DO RIO GRANDE DO SUL
34142 DEFENSORIA PUBLICA DO ESTADO
30814 UNIVERSIDADE ESTADUAL DO RIO GRANDE DO SUL
25422 GOVERNO DO ESTADO
22746 PROCURADORIA-GERAL DO ESTADO
22672 FUNDACAO ESTADUAL DE PROTECAO AMBIENTAL HENRIQUE LUIS ROESSLER
19242 SECRETARIA DE OBRAS SANEAMENTO E HABITACAO
18339 TRIBUNAL DE CONTAS DO ESTADO
16361 FUNDACAO ESTADUAL DE PRODUCAO E PESQUISA EM SAUDE
15907 FUNDACAO DE PROTECAO ESPECIAL DO RIO GRANDE DO SUL
14490 FUNDACAO ESTADUAL DE PLANEJAMENTO METROPOLITANO E REGIONAL
14040 SECRETARIA DA JUSTICA E DOS DIREITOS HUMANOS
13069 SECRETARIA DO AMBIENTE E DESENVOLVIMENTO SUSTENTAVEL
10860 SECRETARIA DE MODERNIZACAO ADMINISTRATIVA E DOS RECURSOS HUMANOS
10653 FUNDACAO GAUCHA DO TRABALHO E ACAO SOCIAL
9192 SECRETARIA DO TRABALHO E DESENVOLVIMENTO SOCIAL
8863 SECRETARIA DE DESENVOLVIMENTO RURAL E COOPERATIVISMO
8746 SUPERINTENDENCIA DE PORTOS E HIDROVIAS
8134 SECRETARIA DE DESENVOLVIMENTO ECONOMICO CIENCIA E TECNOLOGIA
8106 FUNDACAO ZOOBOTANICA DO RIO GRANDE DO SUL
7804 FUNDACAO DE CIENCIA E TECNOLOGIA
7371 FUNDACAO ESTADUAL DE PESQUISA AGROPECUARIA
6938 SUPERINTENDENCIA DO PORTO DE RIO GRANDE
6267 SECRETARIA DA CULTURA
5487 FUNDACAO CULTURAL PIRATINI - RADIO E TELEVISAO
5398 SECRETARIA DO PLANEJAMENTO E DESENVOLVIMENTO REGIONAL
5162 FUNDACAO DE AMPARO A PESQUISA DO ESTADO DO RIO GRANDE DO SUL
5154 SECRETARIA DOS TRANSPORTES
4825 JUSTICA MILITAR DO ESTADO
4807 FUNDACAO ESCOLA TECNICA LIBERATO SALZANO VIEIRA DA CUNHA
4623 FUNDACAO PARA O DESENVOLVIMENTO DE RECURSOS HUMANOS
4600 SECRETARIA DO TURISMO
4202 AGENCIA ESTADUAL DE REGULACAO DOS SERVICOS PUBLICOS DELEGADOS DO RS
4066 JUNTA COMERCIAL DO ESTADO DO RIO GRANDE DO SUL
4017 FUNDACAO AUTARQUICA ORQUESTRA SINFONICA DE PORTO ALEGRE
3450 FUNDACAO DE ARTICULACAO E DESENVOLVIMENTO DE POLITICAS PUBLICAS PARA PPD E PPAH
3376 INSTITUTO DE PREVIDENCIA DO ESTADO DO RIO GRANDE DO SUL
2943 FUNDACAO DE ECONOMIA E ESTATISTICA SIEGFRIED EMANUEL HEUSER
2899 FUNDACAO DE ESPORTE E LAZER DO RIO GRANDE DO SUL
2144 AGENCIA GAUCHA DO DESENVOLVIMENTO E PROMOCAO DO INVESTIMENTO
2082 SECRETARIA DE MINAS E ENERGIA
1217 FUNDACAO TEATRO SAO PEDRO
 998 CONSELHO ESTADUAL DE EDUCACAO
 790 FUNDACAO INSTITUTO GAUCHO DE TRADICAO E FOLCLORE
 194 SECRETARIA DE INFRA-ESTRUTURA E LOGISTICA
 100 SECRETARIA DO DESENVOLVIMENTO E PROMOCAO DO INVESTIMENTO
  90 SECRETARIA DE HABITACAO E SANEAMENTO
  81 SECRETARIA DA CIENCIA, INOVACAO E DESENVOLVIMENTO TECNOLOGICO
  47 SECRETARIA DO ESPORTE E DO LAZER
  44 SECRETARIA DE POLITICAS PARA AS MULHERES
```

# Sincronização e Localização

## Memcached
Para fins de sincronização/localização de qual processo é responsável por cada setor, será utilizado um servidor Memcached (http://memcached.org).
O memcached é um servidor de memória distribuída que possibilita o armazenamento de informações através de chave/valor. Apesar de existirem versões para Windows, os desenvolvedores afirmam que o suporte é apenas para servidores Linux/BSD-like.
O Sistema Distribuído poderá ter 1 ou mais servidores de memcached ativos. Para a realização deste trabalho você pode assumir que teremos apenas 1 servidor em todo o sistema distribuído.
Todas as chaves serão no formato texto e todos os valores em formato JSON.

## Responsabilidades dos Processos

* Cada processo é responsável por incluir/atualizar as chaves no servidor Memcached. Caso o valor de uma chave não exista, ela deve ser criada pelo processo.
* IMPORTANTE: Não utilizar o comando "replace" do Memcached. Para atualizar uma chave realize o "get" dela, atualize o valor e depois faça o "set".
* O processo cliente NÃO DEVE armazenar informações de gastos no Memcached. Apenas o processo servidor deve armazenar dados no Memcached.
* O processo cliente não deve realizar requisições para servidores que não estejam ativos. Além disso, caso tente realizar uma requisição para um servidor e verifique que ele não está mais respondendo, ele deve alterar o status do servidor para inativo (active=false) e atualizar a lista de servidores.

# Arquivo de configuração do servidor

O processo servidor deve possuir um arquivo de configuração com o nome config.json, conforme definido abaixo:

```json
{
  "serverName" : "serverTales",
  "portListen" : 1111,
  "sectorList" : [ 1, 2, 3, 14, 25, 56, 71, 89]
}

```

Onde:
* serverName = Nome idenficador do servidor. Deve ser único em todo o sistema distribuído
* portListen = Número da porta TCP que o servidor deve receber requisições dos clientes
* sectorList = Lista com o código dos setores que o servidor irá responder

# Definição da lista de Chaves a serem armazenadas 

##Chave: SD_ListServers
* Objetivo: Buscar a lista de servidores ativos do sistema distribuído e de quais órgãos ele possui informação
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
            "sectors": [
                {
                	"code" : 1,
                	"description" : "setor 1"
                },
                {
                	"code" : 2,
                	"description" : "setor 2"
                },
                {
                	"code" : 3,
                	"description" : "setor 3"
                }
            ],
            "active": true
        },
        {
            "name": "serverViegas",
            "location": "999.999.999.999:9999",
            "sectors": [
                {
                	"code" : 1,
                	"description" : "setor 1"
                },
                {
                	"code" : 2,
                	"description" : "setor 2"
                },
                {
                	"code" : 3,
                	"description" : "setor 3"
                }
            ],
			"active": false
        }
    ]
}
```

# Chaves de Setor: SD_Sector_&lt;codigoSetor&gt;
* Objetivo: Listar os órgão de cada um dos setores
* Operações: 
  - Buscar a lista de órgãos de um setor
  - Inserir a lista de órgãos de um setor
* Formato:
```json
{
    "sectorCode": 1,
    "sectorDescription": "setor 1",
    "departments": [
        {
            "departmentCode": 1,
            "departmentDescription": "órgão 1"
        },
        {
            "departmentCode": 2,
            "departmentDescription": "órgão 2"
        }
    ]
}
```

# Chaves de Gastos de Setor
  - SD_ExpenseSector_&lt;codigoSetor&gt;_&lt;YYYY&gt;
  - SD_ExpenseSector_&lt;codigoSetor&gt;_&lt;YYYYMM&gt;
  - SD_ExpenseSector_&lt;codigoSetor&gt;_&lt;YYYYMMDD&gt;
* Objetivo: Conter o total de gastos de um setor por ano (YYYY), mês (YYYYMM) ou dia (YYYYMMDD)
* Operações:
  - Buscar o gasto do setor
  - Inserir o gasto do setor
* Formato: 
```json
{
    "sectorCode": 1,
    "sectorDescription": "setor 1",
    "totalExpenses": 999999999.99
}
```

# Chaves de Gastos de Órgão
  - SD_ExpenseDepartment_&lt;codigoSetor&gt; _ &lt;codigoOrgao&gt; _ &lt;YYYY&gt;
  - SD_ExpenseDepartment_&lt;codigoSetor&gt; _ &lt;codigoOrgao&gt; _ &lt;YYYYMM&gt;
  - SD_ExpenseDepartment_&lt;codigoSetor&gt; _ &lt;codigoOrgao&gt; _ &lt;YYYYMMDD&gt;
* Objetivo: Conter o total de gastos de um órgão de um setor por ano (YYYY), mês (YYYYMM) ou dia (YYYYMMDD)
* Operações:
  - Buscar o gasto do órgão
  - Inserir o gasto do órgão
* Formato: 
```json
{
    "sectorCode": 1,
    "sectorDescription": "setor 1",
    "departmentCode": 1,
    "departmentDescription": "department 1",
    "totalExpenses": 999999999.99
}
```

# O cliente do usuário
O cliente deve conectar a um dos servidores disponíveis. Qualquer servidor deve ser apto a receber requisições de qualquer cliente.

# Protocolo de Comunicação entre Cliente e Servidor

Este protocolo deve ser seguido pelos clientes do servidor (seja ele o cliente do usuário ou outro servidor)

## Busca de Setores e Órgãos
* Retorna a lista de setores e órgãos disponíveis para consulta no Sistema Distribuído, independente de qual processo é responsável por cada setor.
### Requisição
```
GETSECTORLIST
```
### Resposta
```json
{
    "sectors": [
        {
            "sectorCode": 1,
            "sectorDescription": "setor 1",
            "departments": [
                {
                    "departmentCode": 1,
                    "departmentDescription": "department 1"
                },
                {
                    "departmentCode": 2,
                    "departmentDescription": "department 2"
                }
            ]
        },
        {
            "sectorCode": 2,
            "sectorDescription": "setor 2",
            "departments": [
                {
                    "departmentCode": 3,
                    "departmentDescription": "department 3"
                },
                {
                    "departmentCode": 4,
                    "departmentDescription": "department 4"
                }
            ]
        }
    ]
}
```

## Total de Gastos de um Setor em um período
* Retorna o total de gastos que um setor realizou em um determinado período

### Requisição
```
GETSECTOR <codigo_setor> <periodo>

onde: 
  - codigo_setor = Código do setor que está sendo pequisado
  - período que deseja pesquisar em um dos seguintes formatos:
    - YYYY -> (ex: 2015) Ano. Mostra todos os gastos durante o ano especificado
    - YYYYMM -> (ex: 201501) Mês. Mostra todos os gastos durante o ano/mês especificado
    - YYYYMMDD -> (ex: 20150101) Dia. Mostra todos os gastos durante o ano/mês/dia especificado
```

### Resposta
````json
{
    "sectorCode": 1,
    "sectorDescription": "setor 1",
    "totalExpenses": 999999999.99
}
````

## Total de Gastos de um Órgão em um período
* Retorna o total de gastos que um órgão de um setor realizou em um determinado período

### Requisição
```
GETDEPARTMENT <codigo_setor>_<codigo_orgao> <periodo>

onde: 
  - codigo_setor = Código do setor que está sendo pequisado
  - codigo_orgao = Código do órgão que está sendo pequisado
  - período que deseja pesquisar em um dos seguintes formatos:
    - YYYY -> (ex: 2015) Ano. Mostra todos os gastos durante o ano especificado
    - YYYYMM -> (ex: 201501) Mês. Mostra todos os gastos durante o ano/mês especificado
    - YYYYMMDD -> (ex: 20150101) Dia. Mostra todos os gastos durante o ano/mês/dia especificado
```

### Resposta
````json
{
    "sectorCode": 1,
    "sectorDescription": "setor 1",
    "departmentCode": 1,
    "departmentDescription": "department 1",
    "totalExpenses": 999999999.99
}
````
