import java.util.ArrayList;
import java.util.List;

public class Day16 implements Day{

    @Override
    public long part1(List<String> lines) {
		Tile[][] contraption = lines.stream()
			.map(String::toCharArray)
			.map(cs -> {
				Tile[] ts = new Tile[cs.length];
				for (int i = 0; i < cs.length; i++) ts[i] = Tile.of(cs[i]);
				return ts;
			})
			.toArray(Tile[][]::new);

		List<Beam> beams = new ArrayList<>();
		List<Beam> addedbeams = new ArrayList<>();
		List<Beam> removedbeams = new ArrayList<>();
		beams.add(new Beam(Dir.Right, 0, -1));

		Dir[][] energized = new Dir[contraption.length][contraption[0].length];

		while (!beams.isEmpty()) {
			for (Beam beam : beams) {
				// step the beam
				switch(beam.dir){
					case Up 	->  beam.x -= 1;
					case Right 	->  beam.y += 1;
					case Down 	->  beam.x += 1;
					case Left 	->  beam.y -= 1;
				}
				// remove outside beams
				boolean outOfBounds = beam.x < 0 || beam.x >= contraption.length || beam.y < 0 || beam.y >= contraption[0].length;
				if( outOfBounds ) {
					removedbeams.add(beam);
					continue;
				}
				// interact with contraption
				switch(contraption[beam.x][beam.y]){
					case Empty -> {}
					case MirrorRWDown -> {
						beam.dir = switch(beam.dir){
							case Up 	-> Dir.Left;
							case Right 	-> Dir.Down;
							case Down 	-> Dir.Right;
							case Left 	-> Dir.Up;
						};
					}
					case MirrorRWUp -> {
						beam.dir = switch(beam.dir){
							case Up 	-> Dir.Right;
							case Right 	-> Dir.Up;
							case Down 	-> Dir.Left;
							case Left 	-> Dir.Down;
						};
					}
					case Horizontal -> {
						if(beam.dir == Dir.Down || beam.dir == Dir.Up){ // split the beam
							addedbeams.add(new Beam(Dir.Right, beam.x, beam.y));
							beam.dir = Dir.Left;
						}
					}
					case Vertical -> {
						if(beam.dir == Dir.Right || beam.dir == Dir.Left){ // split the beam
							addedbeams.add(new Beam(Dir.Up, beam.x, beam.y));
							beam.dir = Dir.Down;
						}
					}
				}

				if(energized[beam.x][beam.y] != null && energized[beam.x][beam.y] == beam.dir){
					if(beam.lastWasAlreadyEnegized) removedbeams.add(beam);
					else beam.lastWasAlreadyEnegized = true;
				}else{
					beam.lastWasAlreadyEnegized = false;
					energized[beam.x][beam.y] = beam.dir;
				}
			}
			beams.removeAll(removedbeams);
			beams.addAll(addedbeams);
			removedbeams.clear();
			addedbeams.clear();
		}

		long sum = 0;
		for (int r = 0; r < energized.length; r++) {
			for (int c = 0; c < energized[0].length; c++) {
				sum += energized[r][c] != null ? 1 : 0;
			}
		}

        return sum;
    }

    @Override
    public long part2(List<String> lines) {
        return 0;
    }

	class Beam{
		Dir dir;
		int x;
		int y;
		boolean lastWasAlreadyEnegized = false;
		public Beam(Dir dir, int x, int y) {
			this.dir = dir;
			this.x = x;
			this.y = y;
		}
		
	}

	enum Dir{
		Up, Right, Down, Left;
	}
    

	enum Tile{
		Empty('.'),
		Vertical('|'),
		Horizontal('-'),
		MirrorRWUp('/'),
		MirrorRWDown('\\');
		
		char symbol;
		Tile(char c){symbol = c;}

		static Tile of(char symbol){
			for (Tile t : Tile.values()) {
				if(t.symbol == symbol) return t;
			}
			assert false : "Bad Symbol: " + symbol;
			return null;
		}
	}
}
