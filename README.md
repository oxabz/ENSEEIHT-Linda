# Projet d'intergiciel pour la 2eme année d'enseeiht.

Small school project aiming to make an implementation of linda tuple space 
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
- `<source-uri>` uri toward the server it mirrors (can be another LindaBackupServer)