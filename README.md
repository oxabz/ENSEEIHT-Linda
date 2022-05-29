# Projet d'intergiciel pour la 2eme année d'enseeiht.

Petit projet en intergiciel visant à reimplementer la biblioteque linda, une biblioteque d'espace de tuple.

## Usage

### LindaServer

```
Usage : LindaServer <port> <path> <resume>
```
- `<port>` of the naming server on witch the linda server will be bound to.
- `<path>` to the save file where linda server will backup its state.
- `<resume>` : boolean designating whether the server should start with the content of the save file

### LindaBackupServer

```
Usage : LindaServer <port> <path> <source-uri>
```
- `<port>` of the naming server on witch the linda server will be bound to.
- `<path>` to the save file where linda server will backup its state.
- `<source-uri>` uri toward the server it mirors (can be an other LindaBackupServer)