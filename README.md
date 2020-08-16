# Industry 4.0

Project simulate modern industry 4.0 factory. Factory adjust production process to achieve the most optimal setup. Production order is based on available machines, production time and priorities. Project uses JADE (Java Agent Development), framework for building multi-agent applications. All data about simulation are loaded from JSON configuration.

Configuration file contains following modules:

- Products - list of products that can be produced. Production consists of multiple stages that must be completed to finish product. Each stage contain list of required actions to complete stage. Order of actions in same stage does not matter. Each action contains list of subproducts required to perform an action. Product is completed when all actions are completed.
- Machines - list of machines available in factory. Each machine contains set of action that can be performed and machine socket identifier. Machine socket is set of machines. Transport between machines in the same machine socket is instant.
- Simulation - contains tasks commissioned to be produces in factory. Each order has time range in which it should be completed and list of ordered products with quantities. Each products has defined priority.
- Breakdowns - list containing time and identifier of machine which fails



## List of agents

---

### Factory Manager

**Role**: Simulation Initializer

Agent reads configuration and initialize Machine Agents. Prepares communication list for each machine.
It is also responsible for sending necessary information to Supervisor and Fitter.

### Machine Agent

---

**Role**: Producer, Principal

For each produced product agent contain list of machines that produce necessary subproducts. Whenever new machine is added to factory, it update list for every machine.
When machine needs specific subproduct, it send request and choose machine that deliver required products first.

### Simulation Agent

---

**Role**: Principal

Agent receives orders from manager, then for each products based on priorities, it send request to machines that can produce specific product. It waits for responses and choose machine that can deliver product first.
After receiving completed product, it report to manager how fast order was finished.

### Breakdown Agent

---

**Role**: MachineFitter

Agent simulate mechanic in factory. After breakdown, it turns off machine for time specified in configuration. After delay, it resume repaired machine. When machine is broke, it redirect received orders to other machines.


## Project demo

![alt text](https://github.com/tomek1kk/Industry4.0/blob/master/factory-demo.JPG?raw=true "Factory")

Sample configurations are defined in files: guitar-factory.json, clothes-factory.json, simple_confiq.json.
