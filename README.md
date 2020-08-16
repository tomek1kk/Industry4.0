## Industry 4.0

Project simulate modern industry 4.0 factory. Factory adjust production process to achieve the most optimal setup. Production order is based on available machines, production time and priorities. Project uses JADE (Java Agent Development), framework for building multi-agent applications. All data about simulation are loaded from JSON configuration.

Configuration file contains following modules:

- Products - list of products that can be produced. Production consists of multiple stages that must be completed to finish product. Each stage contain list of required actions to complete stage. Order of actions in same stage does not matter. Each action contains list of subproducts required to perform an action. Product is completed when all actions are completed.
- Machines - list of machines available in factory. Each machine contains set of action that can be performed and machine socket identifier. Machine socket is set of machines. Transport between machines in the same machine socket is instant.
- Simulation - contains tasks commissioned to be produces in factory. Each order has time range in which it should be completed and list of ordered products with quantities. Each products has defined priority.
- Breakdowns - list containing time and identifier of machine which fails





- Produkty - lista produktów tworzonych w fabryce. Produkcję przedmiotu dzielimy na etapy. Każdy etap zawiera listę wymaganych akcji aby produkt przeszedł na kolejny etap w produkcji. Akcje w jednym etapie mogę być wykonywane w dowolnej kolejności. Akcja zawiera listę podproduktów niezbędnych do wykonania akcji. Produkt uznajemy za gotowy, gdy zostaną nad nim wykonane wszystkie dostępne akcje.
- Maszyny - lista maszyn dostępnych w fabryce. Każda maszyna zawiera zbiór możliwych do wykonania akcji oraz identyfikator gniazda maszyn w którym się znajduje. 
Gniazdo maszyn rozumiemy jako zbiór maszyn. Transport produktów między maszynami z osobnych gniazd maszyn zajmuje czas, natomiast transport w ramach jednego gniazda maszyn jest natychmiastowy.
Każda akcja zawiera informację o produkcie, którego akcja dotyczy - jego nazwa oraz aktualny etap w produkcji. Oprócz tego przechowujemy identyfikator akcji oraz jej czas wykonania.
- Symulacja - zawiera listę obiektów reprezentujących zlecenia dla fabryki. Każde zlecenie zawiera przedział czasowy, w którym powinno zostać zrealizowane oraz listę zamówionych produktów wraz z ilością. Każdy produkt ma ustawiony priorytet.
- Awarie - lista zawierająca przedział czasowy awarii oraz identyfikator maszyny której awaria dotyczy.

### Requirements:
* Job scheduling → “based on Daimler-Chrysler”
* In a factory → groups of ultra modern machines (GoM)
  * Each GoM can do various groups of operations on “prefabricated piece of material”
  * Typically, one GoM is not enough to complete a workflow
    * Completing a product requires multiple GoMs used in a specific order
  * Different requests materialize in an order that cannot be a’priori predicted
  * Various requests may have different “priority levels”
  * Different machines within GoMs can experience problems that will force them out of service, or require them to be (graciously) taken out of service – for limited (not necessarily known in advance) time
* In Daimler-Chrysler case it was shown that placing an agent within each GoM, and adding management agents to the ecosystem, can result in productivity improvement
* Aim of the project is to develop an agent-based “simulator” that will allow modeling and studying such ecosystem


link do schematu znajomości agentów: https://drive.google.com/file/d/1s-KRB-jUtB8HHYWPJhxboZYnUP75Y7pU/view?usp=sharing
