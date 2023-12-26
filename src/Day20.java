import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class Day20 implements Day{

    @Override
    public long part1(List<String> lines) {
        HashMap<String, Module> modules = parseModules(lines);

		final Module button = new Module(Type.Button, "button", new String[]{"roadcaster"});
		boolean done = false;
		int presses = 0;
		int[] count = {0,0};
		List<Entry> todo = new LinkedList<>();

		do{
			todo.add(new Entry(button, false, null));
			while(!todo.isEmpty()){
				Entry e = todo.removeFirst();
				Module current = e.target;
				Module previous = e.previous;
				boolean pulse = e.pulse;
				boolean out = false;
				switch(current.type){
					case Button, Broadcast, Output -> out = false;
					case FlipFlop -> {
						if(pulse) continue;

						out = current.isOff;
						current.isOff = !current.isOff;
					}
					case Conjunction -> {
						current.inputs.put(previous, pulse);
						out = current.inputs.values().stream().allMatch(p -> p) ? false : true;
					}
				}
				for (String t : current.targets) {
					Module m = modules.get(t);
					if(m.type != Type.Output) todo.add(new Entry(m, out, current));
				}
				count[out ? 1 : 0]+=current.targets.size();
			}
			presses++;
			done = true;
			for(Module module : modules.values()){
				switch(module.type){
					case FlipFlop -> done &= module.isOff;
					case Conjunction -> {
						for (boolean p : module.inputs.values()) done &= p == false;
					}
					default -> {}
				}
			}
		}while(!done && presses < 1000);

		int factor = 1000 / presses;
        return count[0]*factor * count[1]*factor;
    }

	record Entry(Module target, boolean pulse, Module previous){}

    @Override
    public long part2(List<String> lines) {
        HashMap<String, Module> modules = parseModules(lines);

		final Module button = new Module(Type.Button, "button", new String[]{"roadcaster"});
		boolean done = false;
		int presses = 0;
		int[] count = {0,0};
		List<Entry> todo = new LinkedList<>();

		do{
			todo.add(new Entry(button, false, null));
			while(!todo.isEmpty()){
				Entry e = todo.removeFirst();
				Module current = e.target;
				Module previous = e.previous;
				boolean pulse = e.pulse;
				boolean out = false;
				switch(current.type){
					case Button, Broadcast, Output -> out = false;
					case FlipFlop -> {
						if(pulse) continue;

						out = current.isOff;
						current.isOff = !current.isOff;
					}
					case Conjunction -> {
						current.inputs.put(previous, pulse);
						out = current.inputs.values().stream().allMatch(p -> p) ? false : true;
					}
				}
				for (String t : current.targets) {
					Module m = modules.get(t);
					if(out == false && m.name == "rx") 
						done = true;
					if(m.type != Type.Output) todo.add(new Entry(m, out, current));
				}
				count[out ? 1 : 0]+=current.targets.size();
			}
			presses++;
			if(presses % 10000 == 0)
				System.out.printf("               \r%,d",presses);
		}while(!done);

        return presses;
    }

	private static HashMap<String, Module> parseModules(List<String> lines){
		HashMap<String, Module> modules;
		List<Module> moduleList = lines.stream()
		.map(l -> {
			String[] parts = l.split(" -> ");
			var t = parts[0].charAt(0);
			var name = parts[0].substring(1);
			var targets = parts[1].split(", ");
			Type type = switch(t){
				case '%' -> Type.FlipFlop;
				case '&' -> Type.Conjunction;
				case 'b' -> Type.Broadcast;
				default -> throw new Error("Bad sign: "+t);
			};
			return new Module(type, name, targets);
		})
		.collect(Collectors.toList());
	
		modules = new HashMap<>(moduleList.size());
		// add all modules
		for (Module module : moduleList) modules.put(module.name, module);
		// add all outputs
		for (Module module : moduleList) for (String target : module.targets) modules.putIfAbsent(target, new Module(Type.Output, target, new String[0]));
		// initialize all conjunctions
		for (Module module : modules.values())
			if(module.type == Type.Conjunction){
				var ins = modules.values()
					.stream()
					.filter(m -> m.targets.contains(module.name))
					.toList();
				for (Module mm : ins) module.inputs.put(mm, false);
			}
		
		return modules;
	}

	enum Type{FlipFlop, Conjunction, Broadcast, Button, Output}

	static class Module{
		final Type type;
		final String name;
		final List<String> targets;
		boolean isOff = true;
		HashMap<Module,Boolean> inputs = new HashMap<>();

		public Module(Type type, String name, String[] targets) {
			this.type = type;
			this.name = name;
			this.targets = new ArrayList<>();
			for (String target : targets) this.targets.add(target);
		}
	}
}
