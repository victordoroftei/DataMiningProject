# Data Mining Project

### Prerequisites:

- Java 17
- Maven 3.8.6
- Un IDE (de preferat IntelliJ sau Eclipse)

### Pasi pentru a putea rula codul:

- Înlocuirea valorii `ABSOLUTE_PATH` în clasa `Indexer` cu calea absolută către directorul root al proiectului.
- După deschiderea proiectului în IDE, este nevoie de rularea comenzii `mvn clean package` în directorul root al proiectului.
- Apoi, pentru crearea indexului, se ruleaza metoda `main()` a clasei `Indexer`. Acest proces a durat, în cazul nostru, aproximativ 2h 30m.
- După ce a fost creat indexul, se pot testa întrebările de Jeopardy, rulând metoda `main()` a clasei `JeopardyClueSearch`.
- Output-ul rezultat va fi similar cu cel din fișierul `jeopardy_initial_output.txt`.

### Cum se folosește indexul deja construit?

- Se descarcă arhiva ce conține indexul de la [adresa](https://drive.google.com/file/d/1vmiJ5x26TYMKOmYjNb_yYdMw-GaFRhuV/view).
- Se dezarhivează conținutul acesteia (ar trebui să fie un folder cu numele `index`).
- Se copiază folderul dezarhivat în rootul proiectului.
