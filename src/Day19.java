import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class Day19 implements Day {

	enum Category {x, m, a, s}

	enum Comparator {
		GreaterThan, LessThan;

		static Comparator of(String s) {
			return switch (s) {
				case ">" -> GreaterThan;
				case "<" -> LessThan;
				default -> throw new Error("Bad comparator sign: " + s);
			};
		}
		@Override
		public String toString() {
			return switch (this) {
				case GreaterThan -> ">";
				case LessThan -> "<";
			};
		}
	}

	static record Rule(Category cat, Comparator comp, int value, String target) {
		public String  toString(){ return "%s %s %d -> %s".formatted(cat, comp, value, target); }

		boolean apply(int[] part) {
			return switch (comp) {
				case GreaterThan -> part[cat.ordinal()] > value;
				case LessThan -> part[cat.ordinal()] < value;
			};
		}

		List<Part> apply(Part part){
			List<Part> res = new ArrayList<>();
			long[] range = part.xmas[cat.ordinal()];
			boolean splitInLowerBound = value > range[0];
			boolean splitInUpperBound = value < range[1];

			switch(comp){
				case GreaterThan -> {
					if(splitInUpperBound){
						if(splitInLowerBound){ // split
							res.add(new Part(part, cat, range[0], value, part.target)); // lower
							res.add(new Part(part, cat, value+1, range[1], target)); // upper
						}else{ // fully applies
							res.add(new Part(part, target));
						}
					}
				}
				case LessThan -> {
					if(splitInLowerBound){
						if(splitInUpperBound){ // split
							res.add(new Part(part, cat, range[0], value-1, target)); // lower
							res.add(new Part(part, cat, value, range[1], part.target)); // upper
						}else{ // fully applies
							res.add(new Part(part, target));
						}
					}
				}
			}
			return res;
		}
	}

	static class Workflow extends ArrayList<Rule> {
		final String name;
		String defaultTarget;
		
		Workflow(String name){ this.name = name; }

		String consider(int[] part) {
			for (Rule r : this) if (r.apply(part)) return r.target;
			return defaultTarget;
		}

		List<Part> consider(Part part){
			List<Part> todo = new ArrayList<>();
			List<Part> next = new ArrayList<>();
			List<Part> done = new ArrayList<>();
			todo.add(part);

			for (Rule rule : this){
				while(!todo.isEmpty()){
					Part p = todo.removeFirst();
					var list = rule.apply(p);
					if(!list.isEmpty()){
						for (Part lp : list) {
							(lp.target.equals(name) ? next : done).add(lp);
						}
					}else{
						todo.add(p);
					}
				}
				todo.addAll(next);
				next.clear();
			}
			todo.replaceAll(p -> p.target.equals(name) ? new Part(p, defaultTarget) : p);
			done.addAll(todo);
			return done;
		}
	}

	static class Entry {
		int[] part;
		String target;

		Entry(int[] p, String t) {
			part = p;
			target = t;
		}
	}

	@Override
	public long part1(List<String> lines) {
		HashMap<String, Workflow> workflows = parseWorkflows(lines);
		List<Entry> entries = new ArrayList<>();

		boolean workflowsParsed = false;
		for (String line : lines) {
			if (!workflowsParsed && line.isEmpty()) {
				workflowsParsed = true;
				continue;
			}
			if (workflowsParsed) {
				String[] lineParts = line.replaceAll("[\\{\\}]", "").split(",");
				int[] values = new int[4];
				for (int i = 0; i < lineParts.length; i++) {
					String part = lineParts[i];
					int value = Integer.parseInt(part.substring(2));
					values[i] = value;
				}
				entries.add(new Entry(values, "in"));
			}
		}

		long sum = 0;
		while (!entries.isEmpty()) {
			Entry e = entries.getFirst();
			loop: while (true) {
				switch (e.target) {
					case "A": sum += e.part[0] + e.part[1] + e.part[2] + e.part[3];
					case "R":
						entries.removeFirst();
						break loop;
					default: e.target = workflows.get(e.target).consider(e.part);
				}
			}
		}

		return sum;
	}

	@Override
	public long part2(List<String> lines) {
		HashMap<String, Workflow> workflows = parseWorkflows(lines);
		

		List<Part> todo = new ArrayList<>();
		todo.add(new Part());

		List<Part> accepted = new ArrayList<>();
		List<Part> rejected = new ArrayList<>();
		
		while(!todo.isEmpty()){
			Part r = todo.removeFirst();
			switch(r.target){
				case "A" : accepted.add(r); break;
				case "R" : rejected.add(r); break;
				default : {
					Workflow w = workflows.get(r.target);
					List<Part> mapped = w.consider(r);
					todo.addAll(mapped);
				}
			}
		}

		long sum = accepted.stream()
			// .peek(System.out::println)
			.mapToLong(Part::value)
			.sum();

		System.out.println();

		long antisum = rejected.stream()
			// .peek(System.out::println)
			.mapToLong(Part::value)
			.sum();
		assert sum + antisum == (long) Math.pow(4000, 4);
		return sum;
	}

	static class Part{
		long[][] xmas = new long[Category.values().length][2];
		String target;
		Part(){
			Arrays.fill(xmas, new long[]{1,4000});
			target = "in";
		}

		Part(Part p, Category cat, long lower,  long upper, String target){
			for (Category c : Category.values())
				xmas[c.ordinal()] = c == cat ? new long[]{lower, upper} : new long[]{p.c(c)[0], p.c(c)[1]};
			this.target = target;
		}

		Part(Part p, String target){
			xmas = new long[][]{
				new long[]{p.x()[0], p.x()[1]},
				new long[]{p.m()[0], p.m()[1]},
				new long[]{p.a()[0], p.a()[1]},
				new long[]{p.s()[0], p.s()[1]}
			};
			this.target = target;
		}

		long[] c(Category c) { return xmas[c.ordinal()]; }

		long[] x() { return xmas[Category.x.ordinal()]; }
		long[] m() { return xmas[Category.m.ordinal()]; }
		long[] a() { return xmas[Category.a.ordinal()]; }
		long[] s() { return xmas[Category.s.ordinal()]; }

		long value(){
			long value = 1;
			for (Category c : Category.values()) {
				long[] range = xmas[c.ordinal()];
				long len = range[1] - range[0] + 1;
				value *= len;
			}
			return value;
		}

		@Override
		public String toString() {
			return String.format("[x:[%4d:%4d], m:[%4d:%4d], a:[%4d:%4d], s:[%4d:%4d]] -> %s", x()[0], x()[1], m()[0], m()[1], a()[0], a()[1], s()[0], s()[1], target);
		}
	}

	static HashMap<String, Workflow> parseWorkflows(List<String> lines){
		HashMap<String, Workflow> workflows = new HashMap<>();

		for (String line : lines) {
			if (line.isEmpty()) break;

			String[] lineParts = line.replace("}", "").split("\\{");
			String key = lineParts[0];
			String[] rules = lineParts[1].split(",");
			Workflow w = new Workflow(key);
			String last = null;
			for (String rule : rules) {
				if (!rule.contains(":")) {
					last = rule;
					break;
				}

				String[] ruleparts = rule.splitWithDelimiters("[\\<\\>:]", 0);
				Category cat = Category.valueOf(ruleparts[0]);
				Comparator comp = Comparator.of(ruleparts[1]);
				int value = Integer.parseInt(ruleparts[2]);
				String targetString = ruleparts[4];
				w.add(new Rule(cat, comp, value, targetString));
			}
			assert last != null : "default rule wasnt set";
			w.defaultTarget = last;
			workflows.put(key, w);
		}
		return workflows;
	}

}
