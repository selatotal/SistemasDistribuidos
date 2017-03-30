# Servidor de Arquivos Distribuídos

## Objetivo

Desenvolver um servidor de arquivos distribuídos.

## Resumo das Funcionalidades

O usuário deve poder:
- Enviar arquivos para o servidor
- Recuperar um arquivo armazenado pelo servidor
- Apagar arquivos armazenados pelo servidor

## Componentes

Deverão ser implementados os seguintes componentes/serviços:

### Clientes

Responsável por receber as solicitações dos usuários, e enviá-las para o servidor de gerenciamento

### Servidor de Gerenciamento

Responsável por receber as requisições dos sistemas clientes e tratá-las de acordo com o solicitado.

### Servidor de Arquivos

Serviço responsável por armazenar fisicamente os arquivos enviados pelo usuário.

## Premissas
- Um arquivo enviado para o Gerenciador deverá ser armazenado em apenas um dos Servidores de Arquivos ativos.
- Não há replicação de arquivos entre os Servidores de Arquivos
- O Gerenciador deve poder detectar se um Servidor de Arquivo está ativo ou não. Caso tente enviar um arquivo para um servidor inativo, deve tentar subir para um servidor ativo.
- O Gerenciador deve ser inicializado antes dos Servidores de Arquivos
- Caso seja desligado um Servidor de Arquivos e o arquivo solicitado pelo usuário estiver neste servidor, o Gerenciador deve avisar que o arquivo está indisponível
- O Sistema de Arquivos deve possuir no máximo 36 arquivos/pastas por pasta
- Todos os arquivos/pastas de um Sistema de Arquivos devem estar sob a mesma pasta raiz (ex: /var/spool/gerenciadorArquivos)
- O sistema distribuído pode ter mais de um Gerenciador ativo. O programa cliente deve poder se comunicar com qualquer um destes gerenciadores
- Não é permitido ter mais de 10% de arquivos em um Servidor de Arquivos em relação aos outros Servidores de Arquivos ativos, desde que tenha um total mínimo de 30 arquivos no sistema distribuído. - Quando um Servidor de Arquivos é iniciado no sistema, a quantidade de arquivos por servidor deve ser balanceada. Esta premissa não é válida nos casos de exclusão de arquivos, mas deve ser verificada no momento da inclusão.
- Os arquivos devem ser armazenados em formato binário nos Servidores de Arquivos.
- Os Servidores de Arquivos podem ser inicializados a qualquer momento (desde que tenha um Gerenciador ativo), inclusive após o sistema distribuído estar funcionando.
- Os programas clientes devem poder funcionar com Gerenciadores desenvolvidos por outros colegas

## Protocolo de Comunicação

O protocolo de comunicação entre o Cliente e o Gerenciador deve ser implementado em modo texto, conorme abaixo:

### Envio de Arquivo
Armazena um arquivo em um dos servidores de Arquivos
#### Requisição

```
PUT <nomeArquivo> <conteudoArquivo>
```
onde:
- <nomeArquivo> - Nome do arquivo a ser armazenado
- <conteudoArquivo> - Conteúdo do arquivo em Base64

Ex:
```
PUT arquivo.txt YXNkYWRhc2Rhc2QgYXNkYSBkYSBzZGEgc2EgYQ==
```
#### Resposta

```
{ 
	"codRetorno": <codigo>,
	"descricaoRetorno": "<descricao>"
}
```
onde:
- <codigo> - Codigo de retorno, de acordo com a tabela de códigos descrita abaixo
- <descricaoRetorno> - Descrição de retorno, de acordo com a tabela de códigos descrita abaixo

Ex:
```
{ 
	"codRetorno": 0,
	"descricaoRetorno": "Requisição OK"
}
```
### Busca de Arquivos
Retorna o conteúdo de um arquivo armazenado
#### Requisição
```
GET <nomeArquivo>
```
onde:
- <nomeArquivo> - Nome do arquivo a ser armazenado

Ex:
```
GET arquivo.txt
```

#### Resposta
```
{ 
	"codRetorno": <codigo>,
	"descricaoRetorno": "<descricao>",
	"conteudo": "<conteudoArquivo>"
}
```
onde:
- <codigo> - Codigo de retorno, de acordo com a tabela de códigos descrita abaixo
- <descricaoRetorno> - Descrição de retorno, de acordo com a tabela de códigos descrita abaixo
- <conteudoArquivo> - Conteúdo do arquivo em Base64

Ex:
```
{ 
	"codRetorno": 0,
	"descricaoRetorno": "Requisição OK",
	"conteudo": "YXNkYWRhc2Rhc2QgYXNkYSBkYSBzZGEgc2EgYQ=="
}
```
### Exclusão de Arquivos
Exclui um arquivo do Servidor de Arquivos
#### Requisição
```
DELETE <nomeArquivo>
```
onde:
- <nomeArquivo> - Nome do arquivo a ser armazenado

#### Resposta
```
{ 
	"codRetorno": <codigo>,
	"descricaoRetorno": "<descricao>"
}
```

Ex:
```
{ 
	"codRetorno": 0,
	"descricaoRetorno": "Requisição OK"
}
```
## Tabela de Códigos

Código  | Descrição              | Usado quando
------- | ---------------------- | ------------
0       | Requisição OK          | Requisição foi executada com sucesso (inclusão/busca/exclusão)
1       | Servidor Indisponível  | Quando não existe nenhum servidor ativo no momento
2       | Arquivo Inexistente    | Quando o usuário tenta buscar ou excluir um arquivo que não existe no sistema
3       | Arquivo Já Existe      | Quando um usuário tenta incluir um arquivo com o mesmo nome de um que já existe no sistema
4       | Arquivo Indisponível   | Quando um arquivo está em um servidor que não está ativo no momento