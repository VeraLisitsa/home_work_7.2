import java.util.*;

public class Main {

    public static final Map<Integer, Integer> sizeToFreq = new HashMap<>();

    public static void main(String[] args) throws InterruptedException {

        int countRoutes = 1000;
        List<Thread> threads = new ArrayList<>();

        for (int i = 0; i < countRoutes; i++) {
            Thread thread = new Thread(() ->
            {
                String str = generateRoute("RLRFR", 100);
                addSizeToMap(str);
            });
            threads.add(thread);
            thread.start();
        }

        for (Thread thread : threads) {
            thread.join();
        }

        LinkedList<Map.Entry<Integer, Integer>> list = new LinkedList<>(sizeToFreq.entrySet());
        list.sort((o1, o2) -> o1.getValue().compareTo(o2.getValue()));
        Map.Entry<Integer, Integer> max = list.getLast();
        while (true) {
            if (list.getLast().getValue() == max.getValue()) {
                System.out.printf("Самое частое количество повторений %d (встретилось %d раз)\n", max.getKey(), max.getValue());
                list.removeLast();
            } else {
                break;
            }
        }
        System.out.println("Другие размеры:");
        for (Map.Entry<Integer, Integer> map : list) {
            System.out.printf("- %d (%d раз)\n", map.getKey(), map.getValue());
        }
    }

    public static String generateRoute(String letters, int length) {
        Random random = new Random();
        StringBuilder route = new StringBuilder();
        for (int i = 0; i < length; i++) {
            route.append(letters.charAt(random.nextInt(letters.length())));
        }
        return route.toString();
    }

    public static void addSizeToMap(String letters) {
        int countR = 0;
        for (int i = 0; i < letters.length(); i++) {
            if (letters.charAt(i) == 'R') {
                countR++;
            }
        }
        synchronized (sizeToFreq) {
            if (sizeToFreq.containsKey(countR)) {
                int countFreqR = sizeToFreq.get(countR) + 1;
                sizeToFreq.put(countR, countFreqR);
            } else {
                sizeToFreq.put(countR, 1);
            }
        }
    }
}