# apiql
API Query Language - lib to convert query language in JPQL query

O apiql permite fazer busca sem escrever a consulta diretamente em PSQL. A consulta é definida a partir de uma configuração seguinte o formato JSON que enviado a API através do parâmetro `filter` da rota padrão `GET/`.

O parâmetro `filter` é capturado na api como uma `String` e convertida para `LinkedTreeMap` (representação genérica de um objeto). O APIQL recebe a filtro e gera a consulta em PSQL, que é executada pelo `EntityManager`.

A rota com o filtro possui a seguinte estrutura: `GET /<endpoint>?filter={attr: {value: 'x', op: 'eq'}}`. No mínimo deve ser inicializado os atributos `value` e `op` (operador), caso contrário o valor deve ser passado direto ao atributo `{attr: 'x'}`.

## Operadores

| Operador  | Equivalente SQL | Exemplo                                                                                                                                                                 |
|-----------|-----------------|-------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| eq        | =               | `{idade: {value: 10, op: eq}}`                                                                                                                                          |
| neq       | !=              | `{idade: {value: 10, op: 'neq'}}`                                                                                                                                       |
| contains  | LIKE            | `{material: {name: {value: Ola, op: 'contains'}}}`                                                                                                                      |
| ncontains | NOT LIKE        | `{material: {name: {value: Ola, op: 'ncontains'}}}`                                                                                                                     |
| gt        | >               | `{material: {name: {qtnEstoque: 20, op: 'gt'}}}`                                                                                                                        |
| lt        | <               | `{material: {name: {qtnEstoque: 20, op: 'lt'}}}`                                                                                                                        |
| in        | in              | `{id: {value: [44], op: 'in'}}`                                                                                                                                     |
| between   | between         | `{dataCriacao: {value: [712836128, 192837129], op: 'between', t: 'timestamp'}}` ou `{dataCriacao: {value: ['2019-10-20', '2020-05-05'], op: 'between', t: 'date'}}` |
|memberOf   | memberOf        | `{"tiposMaterial": { "value": "asd", "op": "memberOf" } }` |

## Conectores

O conector padrão do APIQL é o `and`, porém é possível conectar restrições de consulta usando operador `or`.

Ex: A configuração
```
{
    idade: {
        value: 20,
        op: 'gt'
    },
    {
        value: 70,
        op: 'lt',
        c: 'or'
    }
}
```

vai produzir a seguinte consulta: `SELECT a FROM Pessoa a WHERE a.idade > :aidade or a.peso < :apeso`.

OBS: Use o operador apenas a partir da **segunda restrição**.

Consulta em Java:

```
Map<String, Object> filter = new LinkedTreeMap<String, Object>();
		
Map<String, Object> jpointMaiorQ = new LinkedTreeMap<String, Object>();
jpointMaiorQ.put("value", "10");
jpointMaiorQ.put("op", "gt");
		
Map<String, Object> jpointMenorQ = new LinkedTreeMap<String, Object>();
jpointMenorQ.put("value", "100");
jpointMenorQ.put("op", "lt");
jpointMenorQ.put("c", "or"); 
		
filter.put("idade", jpointMaiorQ);
filter.put("peso", jpointMenorQ);
params.setEntityName("Pessoa");
params.setFilter(filter);
String jpql = objToJPQL.build(params);
```

## Adicionar mais de uma restrição para da atributo

Supondo a consulta dos materiais cujo nome contém os termos **ola** ou **deter**, podemos construir a consulta da repetindo a restrição numa lista:

`{material: [{name: {value: 'ola', op: 'contains'}}, {name: {value:'deter', op: 'contains', c: 'or'}}]}`

