# Servidor de Banco de Dados Distribuídos

## Objetivo

Desenvolver um servidor de banco de dados distribuído, juntamente com uma ferramenta de cache para aumentar a performance.

## Resumo das Funcionalidades

O usuário deve poder:
- Buscar dados de alunos e turmas
- Cadastrar dados de alunos e turmas
- Excluir dados de alunos e turmas
- Todos os componentes devem poder requisições de mais de um cliente ao mesmo tempo

## Componentes

Deverão ser implementados os seguintes componentes/serviços:

### Clientes

Responsável por receber as solicitações dos usuários, e enviá-las para um servidor de cache.

### Servidor de Cache

Deve responder às solicitações do usuário e retornar a resposta adequada.
Caso a solicitação seja de busca de dados, deve tomar uma das seguintes decisões:
* Caso a informação solicitada esteja armazenada em cache local e a data de expiração ainda não tenha sido atingida, deve retornar a informação diretamente para o usuário sem fazer outra ação.
* Caso a informação solicitada NÃO ESTEJA ARMAZENADA em cache local, deve solicitar a informação para o servidor de gerenciamento, armazenar a resposta do servidor em cache local com a data/hora da solicitação realizada e devolver a resposta para o usuário.
* Caso a informação solicitada esteja armazenada em cache local mas A DATA DE EXPIRAÇÃO FOI ATINGIDA, deve excluir a informação do cache local e solicitar novamente ao servidor a informação solicitada.
Caso a solicitação seja de inclusão ou exclusão de dados por parte do usuário, deve excluir a informação do cache (caso ela exista) e realizar a operação no servidor de gerenciamento.
* O servidor de cache deve utilizar um cache em memória. Caso o servidor de cache seja reiniciado, as informações cacheadas devem ser perdidas.

### Servidor de Gerenciamento

Responsável por receber as requisições dos sistemas clientes ou do servidor de cache e tratá-las de acordo com o solicitado.
Este servidor mantém as informações de onde estão os servidores que contém os dados de turmas e de alunos. De acordo com a requisição recebida, ele deve tratar conforme solicitado.

### Servidor de Dados de Alunos/Turmas

Serviço responsável por armazenar fisicamente os dados de alunos/turmas criados pelos usuários. Este armazenamento é permanente, ou seja, caso o servidor seja reiniciado os dados devem ser mantidos. Para este trabalho, não deve ser utilizado nenhum tipo de SGBD, seja SQL ou No-SQL. A implementação da persistência deve ser feita pelo aluno.

## Premissas
- A ordem de inicialização dos servidores deve ser: Servidores de Dados de Alunos/Turmas, Servidor de Gerenciamento, Servidor de Cache.
- Cada um dos servidores deve possuir um arquivo de configuração, conforme descrito abaixo. 
- Todos os servidores/clientes devem poder funcionar com servidores/clientes desenvolvidos por outros colegas

## Arquivos de Configuração
Os arquivos de configuração devem ser criados em modo texto, no formato JSON, e devem ter, APENAS, as configurações abaixo (definidas para cada tipo de serviço).
Caso algum aluno ache necessário uma configuração adicional, deve conversar com o professor.
Os arquivos devem poder ser alterados sem a necessidade de compilar o código novamente.

### Servidor de Cache

```text
{
	"port": 1234,                      // Porta onde vai aceitar requisições
	"managerServerHost": "127.0.0.1",  // Endereço IP ou Host do servidor de gerenciamento
	"managerServerPort": 1235,         // Porta onde o servidor de gerenciamento recebe requisições
	"cacheTimeout": 10000              // Tempo (em ms) de expiração do cache local
}
```

### Servidor de Gerenciamento

```text
{
	"port": 1235,                      // Porta onde vai aceitar requisições
	"studentServerHost": "127.0.0.1",  // Endereço IP ou Host do servidor de dados de alunos
	"studentServerPort": 1236,         // Porta onde o servidor de dados de alunos recebe requisições
	"classServerHost": "127.0.0.1",    // Endereço IP ou Host do servidor de dados de turmas
	"classServerPort": 1237            // Porta onde o servidor de dados de turmas recebe requisições
}
```

### Servidor de Alunos/Turmas

```text
{
	"port": 1236,                    // Porta onde vai aceitar requisições
	"datafile": "/tmp/student.data"  // Localização do arquivo de dados
}
```


## Protocolo de Comunicação

O protocolo de comunicação deve ser implementado em modo texto, conforme abaixo:

### Cadastrar Turma
Cadastra uma turma no sistema
#### Requisição

```
/incluiTurma/<idTurma>/<nomeTurma>
```
onde:
- <idTurma> - Id da turma (não deve permitir o cadastro de duas turmas com o mesmo ID)
- <nomeTurma> - Nome da Turma

Ex:
```
/incluiTurma/1/Banco de Dados
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

### Cadastrar Aluno
Cadastra um aluno no sistema.
#### Requisição

```
/incluiAluno/<idAluno>/<nomeAluno>/<listaDeTurmas>
```
onde:
- <idAluno> - Id do aluno (não deve permitir o cadastro de dois alunos com o mesmo ID)
- <nomeAluno> - Nome do aluno
- <listaDeTurmas> - Lista de IDs de turmas, separados por vírgula, sem espaço. Não deve permitir o cadastro do aluno caso não exista uma turma cadastrada

Ex:
```
/incluiAluno/1/Tales Viegas/1,2,3,4
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

### Busca de Turmas
Retorna os dados de uma determinada turma
#### Requisição
```
/turma/<idTurma>
```
onde:
- <idTurma> - ID da turma a ser pesquisada

Ex:
```
/turma/1
```

#### Resposta do Servidor de Gerenciamento
```
{ 
	"idTurma": <idTurma>,
	"nomeTurma": "<nomeTurma>",
	"alunos": [
		{
			"idAluno": <idAluno1>,
			"nomeAluno": "<nomeAluno1>",
		},
		{
			"idAluno": <idAluno1>,
			"nomeAluno": "<nomeAluno1>",
		}
	]
}
```
onde:
- <idTurma> - id da turma
- <nomeTurma> - Nome da turma
- <alunos> - Lista de alunos matriculados da turma

Ex:
```
{ 
	"idTurma": 1,
	"nomeTurma": "Banco de Dados",
	"alunos": [
		{
			"idAluno": 1,
			"nomeAluno": "Tales Viegas"
		},
		{
			"idAluno": 2,
			"nomeAluno": "Carlos Zeve"
		}
	]
}
```

#### Resposta do Servidor de Turmas
```
{ 
	"idTurma": <idTurma>,
	"nomeTurma": "<nomeTurma>"
}
```
onde:
- <idTurma> - id da turma
- <nomeTurma> - Nome da turma

Ex:
```
{ 
	"idTurma": 1,
	"nomeTurma": "Banco de Dados"
}
```

### Busca de Alunos
Retorna os dados de um determinado aluno
#### Requisição
```
/aluno/<idAluno>
```
onde:
- <idAluno> - ID do aluno a ser pesquisado

Ex:
```
/aluno/1
```

#### Resposta do Servidor de Gerenciamento
```
{ 
	"idAluno": <idAluno>,
	"nomeAluno": "<nomeAluno>",
	"turmas": [
		{
			"idTurma": <idTurma1>,
			"nomeTurma": "<nomeTurma1>"
		},
		{
			"idTurma": <idTurma1>,
			"nomeTurma": "<nomeTurma1>"
		}
	]
}
```
onde:
- <idAluno> - id do Aluno
- <nomeAluno> - Nome do aluno
- <turmas> - Lista de turmas onde o aluno está matriculado

Ex:
```
{ 
	"idAluno": 1,
	"nomeAluno": "Tales Viegas",
	"turmas": [
		{
			"idTurma": 1,
			"nomeTurma": "Banco de Dados"
		},
		{
			"idTurma": 2,
			"nomeTurma": "Compiladores"
		}
	]
}
```

#### Resposta do Servidor de Aluno
```
{ 
	"idAluno": <idAluno>,
	"nomeAluno": "<nomeAluno>",
	"turmas": [
		{
			"idTurma": <idTurma1>,
		},
		{
			"idTurma": <idTurma1>,
		}
	]
}
```
onde:
- <idAluno> - id do Aluno
- <nomeAluno> - Nome do aluno
- <turmas> - Lista de turmas onde o aluno está matriculado

Ex:
```
{ 
	"idAluno": 1,
	"nomeAluno": "Tales Viegas",
	"turmas": [
		{
			"idTurma": 1,
		},
		{
			"idTurma": 2,
		}
	]
}
```

### Exclusão de Turma
Exclui uma turma da base. Devem ser excluídos todos os vínculos entre alunos e turmas caso existam.

#### Requisição
```
/apagaTurma/<idTurma>
```
onde:
- <idTurma> - Id da turma a ser excluída

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

### Exclusão de Aluno
Exclui um aluno da base. Devem ser excluídos todos os vínculos entre alunos e turmas caso existam.

#### Requisição
```
/apagaAluno/<idAluno>
```
onde:
- <idAluno> - Id do aluno a ser excluído

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

### Busca de Todas as Turmas
Retorna os dados de todas as turmas cadastradas
#### Requisição
```
/turmas
```

#### Resposta do Servidor de Gerenciamento
```
{
	"turmas": [
		{
			"idTurma": <idTurma>,
			"nomeTurma": "<nomeTurma>",
			"alunos": [
				{
					"idAluno": <idAluno1>,
					"nomeAluno": "<nomeAluno1>",
				},
				{
					"idAluno": <idAluno1>,
					"nomeAluno": "<nomeAluno1>",
				}
			]
		},
		{
			"idTurma": <idTurma>,
			"nomeTurma": "<nomeTurma>",
			"alunos": [
				{
					"idAluno": <idAluno1>,
					"nomeAluno": "<nomeAluno1>",
				},
				{
					"idAluno": <idAluno1>,
					"nomeAluno": "<nomeAluno1>",
				}
			]
		},
		{
			"idTurma": <idTurma>,
			"nomeTurma": "<nomeTurma>",
			"alunos": [
				{
					"idAluno": <idAluno1>,
					"nomeAluno": "<nomeAluno1>",
				},
				{
					"idAluno": <idAluno1>,
					"nomeAluno": "<nomeAluno1>",
				}
			]
		}
	] 
}
```

Ex:
```
{
	"turmas" : [
		{ 
			"idTurma": 1,
			"nomeTurma": "Banco de Dados",
			"alunos": [
				{
					"idAluno": 1,
					"nomeAluno": "Tales Viegas"
				},
				{
					"idAluno": 2,
					"nomeAluno": "Carlos Zeve"
				}
			]
		},
		{ 
			"idTurma": 2,
			"nomeTurma": "Compiladores",
			"alunos": [
				{
					"idAluno": 1,
					"nomeAluno": "Tales Viegas"
				},
				{
					"idAluno": 2,
					"nomeAluno": "Carlos Zeve"
				}
			]
		}
	] 
}
```

#### Resposta do Servidor de Turmas
```
{
	"turmas": [
		{
			"idTurma": <idTurma>,
			"nomeTurma": "<nomeTurma>"
		},
		{
			"idTurma": <idTurma>,
			"nomeTurma": "<nomeTurma>"
		},
		{
			"idTurma": <idTurma>,
			"nomeTurma": "<nomeTurma>"
		}
	] 
}
```

Ex:
```
{
	"turmas" : [
		{ 
			"idTurma": 1,
			"nomeTurma": "Banco de Dados"
		},
		{ 
			"idTurma": 2,
			"nomeTurma": "Compiladores"
		}
	] 
}
```

### Busca de Todos os Alunos
Retorna os dados de todos os alunos cadastrados
#### Requisição
```
/alunos
```

Ex:
```
/alunos
```

#### Resposta do Servidor de Gerenciamento
```
{ 
	"alunos": [
		{
			"idAluno": <idAluno>,
			"nomeAluno": "<nomeAluno>",
			"turmas": [
				{
					"idTurma": <idTurma1>,
					"nomeTurma": "<nomeTurma1>"
				},
				{
					"idTurma": <idTurma1>,
					"nomeTurma": "<nomeTurma1>"
				}
			]
		},
		{
			"idAluno": <idAluno>,
			"nomeAluno": "<nomeAluno>",
			"turmas": [
				{
					"idTurma": <idTurma1>,
					"nomeTurma": "<nomeTurma1>"
				},
				{
					"idTurma": <idTurma1>,
					"nomeTurma": "<nomeTurma1>"
				}
			],
		}
	]
}
```

Ex:
```
{ 
	"alunos": [
		{
			"idAluno": 1,
			"nomeAluno": "Tales Viegas",
			"turmas": [
				{
					"idTurma": 1,
					"nomeTurma": "Banco de Dados"
				},
				{
					"idTurma": 2,
					"nomeTurma": "Compiladores"
				}
			]
		},
		{
			"idAluno": 2,
			"nomeAluno": "Carlos Zeve",
			"turmas": [
				{
					"idTurma": 1,
					"nomeTurma": "Banco de Dados"
				},
				{
					"idTurma": 2,
					"nomeTurma": "Compiladores"
				}
			]
		}
	]
}
```

#### Resposta do Servidor de Alunos
```
{ 
	"alunos": [
		{
			"idAluno": <idAluno>,
			"nomeAluno": "<nomeAluno>",
			"turmas": [
				{
					"idTurma": <idTurma1>,
				},
				{
					"idTurma": <idTurma1>,
				}
			]
		},
		{
			"idAluno": <idAluno>,
			"nomeAluno": "<nomeAluno>",
			"turmas": [
				{
					"idTurma": <idTurma1>,
				},
				{
					"idTurma": <idTurma1>,
				}
			],
		}
	]
}
```

Ex:
```
{ 
	"alunos": [
		{
			"idAluno": 1,
			"nomeAluno": "Tales Viegas",
			"turmas": [
				{
					"idTurma": 1,
				},
				{
					"idTurma": 2,
				}
			]
		},
		{
			"idAluno": 2,
			"nomeAluno": "Carlos Zeve",
			"turmas": [
				{
					"idTurma": 1,
				},
				{
					"idTurma": 2,
				}
			]
		}
	]
}
```


## Tabela de Códigos

Código  | Descrição              | Usado quando
------- | ---------------------- | ------------
0       | Requisição OK          | Requisição foi executada com sucesso (inclusão/busca/exclusão)
1       | Registro Já Cadastrado | Quando tenta-se cadastrar um aluno ou turma com o mesmo ID
2       | Erro de Relacionamento | Quando tenta-se cadastrar um aluno em uma turma que não existe
3       | Servidor Indisponível  | Algum dos componentes dos quais o serviço necessita não está disponível no momento
4       | Registro Não Encontrado| Quando se tenta consultar um registro que não existe
5       | Requisição Inválida     | Quando é feita uma requisição que o servidor não entende

