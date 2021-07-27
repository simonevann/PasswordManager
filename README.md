# PasswordManager
Software per la creazione di un portachiavi criptato via riga di comando
Viene generato un file criptato per ogni utente, rendendo portabili le informazioni.
## Cifratura dati
La cifratura viene eseguita tramite algoritmo AES.
La cifratura/decifratura utilizza i seguenti dati:
- Username => in chiaro
- Password => parola segreta nota solo all'utente
- Salt => Stringa generata random per ogni nuovo utente, serve per evitare attacchi brute force
- Pepper => Stringa cosante di caratteri definita all'interno del programma, evita che venga utilizzato un altro software per la decifratura

## To Do
- Creazione interfaccia grafica desktop
- Implementazione 2FA

## File dati
I dati vengono salvati su file nominato come l'username e con estensione .dat
### Struttura (v.1.0):
- 1 byte => versione
- 150 byte => salt
- 1 byte => separatore
- Dati in formato csv cifrati

## File di configurazione
Il file di configurazione configuration.conf è in formato csv, qui vi si trovano i dati per la configurazione del software
### Struttura:
- UI => Tipo di interfaccia, 0 per la CLI, 1 quando sarà disponibile la GUI
- URI => Path dove loggere e scrivere i file utente

