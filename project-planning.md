---
# Raport wstępny
##### Palina Hrynko, Tomasz Kostowski, Krzysztof Kicun
---
## Opis projektu
* Wraz z rozwojem technologii zmienia się przemysł. Świat doświadczył już trzech rewolucji przemysłowych, w tej chwili, na naszych oczach, dokonuje się czwarta związana z rozwojem internetu oraz zanikaniem bariery pomiędzy człowiekiem a maszyną.
    W ramach naszego projektu chcemy stworzyć symulator fabryki spełniającej wymagania przemysłu 4.0, to znaczy:
    - pracują w niej inteligentne maszyny, zdolne zmienić typ produkowanego półproduktu/produktu w bardzo krótkim czasie, bez potrzeby ingerencji operatora
    - wewnętrzy transport w fabryce realicują autonomiczne wózki (nie będziemy się na nich skupiać w naszym symulatorze), brak tradycyjnej linii produkcyjnej
    - fabryka będzie mogła produkować wiele typów produktów jednocześnie oraz będzie zdolna do szybkiego przestawienia produkcji w reakcji na zmiany popytu na rynku
    - wiele bezprzewodowych czujników służących do monitorowania produkcji i zużycia energii, dane z tych czujników są wykorzystywane do optymalizacji produkcji (zmniejszenie zużycia prądu, zmiejszenie ilości zmagazynowanych półproduktów i materiałów)
    Nasz symulator ma wykorzystywać podejście agentowe i w założeniach ma umożliwić oszacowanie zysków (lub strat) z zastosowania takiego podejścia oraz innowacji przemysłu 4.0.


* W projekcie będziemy wczytywać wymagania z pliku json/xml. Fabryka będzie się składać z poszczególnych agentów wykonujących sprecyzowaną akcję + agentów-asystentów które będą odpowiedzialne za rozwiązywanie pojawiających się problemów fabryki (np. Agent potrzebuje zamiany, deadlock lub inne), modyfikacji początkowych wymagań klienta oraz aktorów, występujących w roli “czujników” fabryki, które przekazują swoje informacje asystentom. Przewidujemy również moduł zarządzający który wczyta wymagania i uruchomi wszystkich agentów


## Plan działania
Kolejnym krokiem będzie rozpoczęcie implementacji naszego rozwiązania. Chcemy zacząć od zbudowania podstawowego systemu zawierającego moduł zarządzający, kilka agentów oraz prostą komunikację między nimi. Program powinien odczytywać pliki konfiguracyjne, które docelowo będą reprezentować konkretny stan naszej fabryki. Przy implementacji będziemy zwracali uwagę, aby system był jak najbardziej otwarty na nowe funkcjonalności lub modyfikacje, które być może postanowimy dodać w trakcie projektu.

## Pytania
* Założyliśmy, że system będzie w pełni uogólniony i zależał jedynie od wprowadzonej konfiguracji. Czy takie podejście do tematu jest poprawne, a może powinniśmy wybrać konkretny rodzaj fabryki i do niej dopasować konkretne komponenty (maszyny, czujniki itp.)?

## Materiały
* https://przemysl-40.pl/wp-content/uploads/2018-Od-Industry-4.0-do-Smart-Factory.pdf?fbclid=IwAR2YLQg0IKb1hF-6zvRZ9Rc3tPgk8-BLyL0klefOIDLCDZ20akBuMUl6z10
* https://www.daimler.com/innovation/case/connectivity/industry-4-0.html
* https://www.youtube.com/watch?v=4XLqdYZWq9I&fbclid=IwAR0Vj7iarAVDCY-NNVKn3YeFq8aNvDtjm7F5DLwT99jQo5f0psty-mOkUok
* https://www.youtube.com/watch?v=4l_THuN9QxE&fbclid=IwAR3UJxVPlo92zlTPMQ33TFdKuxUGMYS2_JwIwknJH4A-eQsA7dcq0M7K7UU
* https://www.sciencedirect.com/science/article/pii/S2405896318314332
* https://www.youtube.com/watch?v=uwfuYam8-Wk&fbclid=IwAR2lc43zLPhmo_TiskCp5htvq4wHz9PNFbIteyK4_WkC2goYzxlJwwndNwY



