import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Advent {
    public static void main(String[] args) {
        //  runDay(new Day01(),"Trebuchet?!");
        //  runDay(new Day02(),"Cube Conundrum");
        //  runDay(new Day03(),"Gear Ratios");
        //  runDay(new Day04(),"Scratchcards");
        //  // runDay(new Day05(),"If You Give A Seed A Fertilizer"); // TODO Part 2 Performance
        //  runDay(new Day06(),"Wait For It");
        //  runDay(new Day07(),"Camel Cards"); // TODO Part 2
        //  runDay(new Day08(),"Haunted Wasteland");
        //  runDay(new Day09(),"Mirage Maintenance");
        //  runDay(new Day10(),"Pipe Maze");
        //  runDay(new Day11(),"Cosmic Expansion");
        //  // runDay(new Day12(),"Hot Springs"); // TODO unsolved
        //  runDay(new Day13(),"Point of Incidence");
        //  runDay(new Day14(),"Parabolic Reflector Dish");
        //  runDay(new Day15(),"Lens Library");
        //  runDay(new Day16(),"The Floor Will Be Lava");
        // runDay(new Day17(),"Clumsy Crucible");
        runDay(new Day18(),"Lavaduct Lagoon");
        // runDay(new Day19(),"");
        // runDay(new Day20(),"");
        // runDay(new Day21(),"");
        // runDay(new Day22(),"");
        // runDay(new Day23(),"");
        // runDay(new Day24(),"");
        // runDay(new Day25(),"");
    }

    static void runDay(Day day, String title) {
        try {
            String name = day.getClass().getSimpleName();
            String num = name.substring(3);
            var in = Files.readAllLines(Path.of("./in/in"+num+".txt"));
            System.out.println("\n"+name+": "+title);
			long part1 = day.part1(in);
            System.out.printf("  Part 1: %,18d | %14d \n", part1, part1);
			long part2 = day.part2(in);
            System.out.printf("  Part 2: %,18d | %14d \n", part2, part2);
        } catch (IOException e) {
            System.out.println("Please create the file "+e.getMessage());
        }
    }
}
