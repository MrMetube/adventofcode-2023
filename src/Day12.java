import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Day12 implements Day {

	@Override
	public long part1(List<String> lines) {
		long arrangements = lines.stream().mapToLong(l -> {
			String[] record = l.split(" ");
			List<Integer> conditions = Arrays.stream(record[1].split(",")).mapToInt(Integer::parseInt).boxed().collect(Collectors.toList());
			// String springs = record[0].replaceAll("\\?","I").replaceAll("[.]","o");
			String springs = reduceOperationalSprings(record[0]);
			
			return 0;
		}).sum();

		return arrangements;
	}

	@Override
	public long part2(List<String> lines) {
		return 0;
	}

	

	private static String reduceOperationalSprings(String s) {
		return s.replaceAll("\\.+", "\\.").replaceAll("^\\.", "").replaceAll("\\.$", "");
	}

	private static long nChooseK(long n, long k) {
		return factorial(n) / (factorial(k) * factorial(n - k));
	}

	private static long factorial(long a) {
		long fac = 1;
		for (long i = a; i > 1; i--)
			fac *= i;
		return fac;
	}
}
