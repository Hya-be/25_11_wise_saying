import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

class App {
  Scanner scanner;
  int lastWiseSayingId;
  List<Quotation> quotations;

  App() {
    scanner = new Scanner(System.in);
    lastWiseSayingId = 0;
    quotations = new ArrayList<>();
  }

  void run() {
    System.out.println("== 명언 앱 ==");

    while (true) {
      System.out.print("명령) ");
      String cmd = scanner.nextLine();

      if (cmd.equals("종료")) {
        break;
      }
      else if (cmd.equals("등록")) {
        actionWrite();
        System.out.printf("%d번 명언이 등록되었습니다.\n", lastWiseSayingId);
      }
      else if (cmd.equals("목록")) {
        actionList();
      }
      else if (cmd.startsWith("삭제?")){ //cmd에서 삭제? 시작하는가? true면 함수 호출
        actionRemove(cmd);
      }
    }
  }

  void actionWrite() {
    System.out.print("명언 : ");
    String content = scanner.nextLine();

    System.out.print("작가 : ");
    String authorName = scanner.nextLine();

    lastWiseSayingId++;

    int id = lastWiseSayingId;

    Quotation quotation = new Quotation(id, content, authorName);

    quotations.add(quotation);
  }

  void actionList() {
    System.out.println("번호 / 명언 / 작가");
    System.out.println("--------------------");

    if (quotations.isEmpty()) {
      System.out.println("등록된 명언이 없습니다.");
    }
    for (int i = quotations.size() - 1; i >= 0; i--) {
      Quotation quotation = quotations.get(i);
      System.out.printf("%d / %s / %s\n", quotation.id, quotation.content, quotation.authorName);
    }
  }

  void actionRemove(String cmd){//매개변수 cmd에 삭제?id=1 문자열이 들어있다면
    String idStr = cmd.replace("삭제?id=", "");// 삭제?id= 부분만 "" 빈문자열로 대체, 결국 "1"만 남게된다
    int id = Integer.parseInt(idStr); //idStr변수에는 "1" 문자열 1이 저장되어있고, Integer.parseInt(idStr) 통해서 "1"이 숫자 1로 바뀌고, id 변수에 저장된다.
    System.out.printf("%d번 명언을 삭제합니다.\n", id);
  }
}