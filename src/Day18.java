import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Day18 implements Day{

    @Override
    public long part1(List<String> lines) {
		Order[] orders = lines.stream()
			.map(l -> l.replaceAll("[()#]",""))
			.map(l -> l.split(" "))
			.map(l -> {
				char dir = l[0].charAt(0);
				int len = l[1].charAt(0) - '0';
				int rgb = Integer.parseInt(l[2],16);
				return new Order(dir, len, (rgb>>16), ((rgb&0xFF00)>>8), (rgb&0xFF));
			})
			.toArray(Order[]::new);


		int x = 0, y = 0, xmin = 999, xmax = 0, ymin = 999, ymax = 0;
		for(Order order : orders){
			switch(order.dir){
				case 'U' -> x-=order.len;
				case 'D' -> x+=order.len;
				case 'L' -> y-=order.len;
				case 'R' -> y+=order.len;
			}
			xmin = Math.min(xmin, x);
			xmax = Math.max(xmax, x);
			ymin = Math.min(ymin, y);
			ymax = Math.max(ymax, y);
		}
		int rows = xmax - xmin +1;
		int cols = ymax - ymin +1;
		char[][] lagoon = new char[rows*4][cols*4+1];
		x = rows*2;
		y = rows*2;
		int startX = x;
		int startY = y;
		for (char[] cs : lagoon) Arrays.fill(cs, '.');
		
		x = startX;
		x = startY;
		for(Order order : orders){
			switch(order.dir){
				case 'U' -> {
					for (int i = 0; i < order.len; i++) {
						lagoon[x][y] = '#';
						x--;
					}
				}
				case 'D' -> {
					for (int i = 0; i < order.len; i++) {
						lagoon[x][y] = '#';
						x++;
					}

				}
				case 'L' -> {
					for (int i = 0; i < order.len; i++) {
						lagoon[x][y] = '#';
						y--;
					}

				}
				case 'R' -> {
					for (int i = 0; i < order.len; i++) {
						lagoon[x][y] = '#';
						y++;
					}

				}
			}
		}
		{ // reduce the empty space
			lagoon = Arrays.stream(lagoon)
				.filter(r -> !String.valueOf(r).chars().allMatch(c -> c=='.'))
				.toArray(char[][]::new);
			char[][] flipped = new char[lagoon[0].length][lagoon.length];
			for (int r = 0; r < lagoon.length; r++) {
				for (int c = 0; c < lagoon[0].length; c++) {
					flipped[c][r] = lagoon[r][c];
				}
			}
			lagoon = Arrays.stream(flipped)
				.filter(r -> !String.valueOf(r).chars().allMatch(c -> c=='.'))
				.toArray(char[][]::new);
			flipped = new char[lagoon[0].length][lagoon.length];
			for (int r = 0; r < lagoon.length; r++) {
				for (int c = 0; c < lagoon[0].length; c++) {
					flipped[c][r] = lagoon[r][c];
				}
			}
			lagoon = flipped;
		}

		for (char[] cs : lagoon) System.out.println(cs);
		System.out.println();
		// fill exterior
		List<int[]> todo = new ArrayList<>();
		for (int r = 0; r < lagoon.length; r++) {
			if(lagoon[r][0] != '#') todo.add(new int[]{r,0});
			if(lagoon[r][lagoon[0].length-1] != '#') todo.add(new int[]{r,lagoon[0].length-1});
		}
		for (int c = 0; c < lagoon[0].length; c++) {
			if(lagoon[0][c] != '#') todo.add(new int[]{0,c});
			if(lagoon[lagoon.length-1][c] != '#') todo.add(new int[]{lagoon.length-1,c});
		}
		while (!todo.isEmpty()) {
			int[] pos = todo.removeFirst();
			int px = pos[0];
			int py = pos[1];
			if(px < 0 || px >= lagoon.length || py < 0 || py >= lagoon[0].length) continue;
			if(lagoon[px][py] == '#' || lagoon[px][py] == '?') continue;
			lagoon[px][py] = '?';
			todo.add(new int[]{px+1, py});
			todo.add(new int[]{px-1, py});
			todo.add(new int[]{px, py+1});
			todo.add(new int[]{px, py-1});
		}
		for (char[] cs : lagoon) System.out.println(cs);
		System.out.println();

		long size = 0;
		for (char[] row : lagoon) {
			for (char c : row) {
				size += c != '?' ? 1 : 0;
			}
		}

        return size;
    }

    @Override
    public long part2(List<String> lines) {
        return 0;
    }

	static record Order(char dir, int len, int r, int g, int b){}
    
}
