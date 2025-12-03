import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * App (명언 앱)
 * - 콘솔 기반으로 명언을 등록/목록/삭제/종료하는 간단한 애플리케이션의 메인 로직 클래스.
 * - 사용자 입력을 받아 명령어를 해석(Rq 사용)하고 해당 기능을 수행한다.
 */
class App {
  // 콘솔로부터 문자열 입력을 받기 위한 Scanner
  Scanner scanner;

  // 마지막으로 발급된 명언 고유번호 (자동 증가용 카운터)
  int lastWiseSayingId;

  // 등록된 명언들을 저장하는 리스트 (메모리 내 저장)
  List<Quotation> quotations;

  /**
   * 생성자
   * - 입력용 Scanner 초기화
   * - ID 카운터 초기화 (0부터 시작하여 등록 시 증가)
   * - 명언 저장소(리스트) 초기화
   */
  App() {
    scanner = new Scanner(System.in);
    lastWiseSayingId = 0;
    quotations = new ArrayList<>();
  }

  /**
   * 애플리케이션 실행 루프
   * - "== 명언 앱 ==" 헤더를 출력
   * - 무한 루프에서 사용자의 명령어를 한 줄 단위로 입력 받음
   * - 입력된 명령어를 Rq로 파싱하여 action(동작)과 파라미터를 분리
   * - action에 따라 등록/목록/삭제/종료를 수행
   */
  void run() {
    System.out.println("== 명언 앱 ==");

    // 사용자가 '종료'를 입력할 때까지 반복
    while (true) {
      System.out.print("명령) ");
      String cmd = scanner.nextLine();   // 콘솔에서 한 줄 입력 받기

      // Rq: "등록", "목록", "삭제?id=1" 같은 문자열을 액션/파라미터로 파싱하는 헬퍼
      Rq rq = new Rq(cmd);

      // 파싱된 액션 문자열에 따라 분기
      switch (rq.getAction()) {
        case "종료":
          // 메서드 종료 -> 프로그램 종료
          return;

        case "등록":
          // 명언 등록 절차 수행 (내용/작가 입력받아 저장)
          actionWrite();
          break;

        case "목록":
          // 현재까지 등록된 명언 리스트를 출력 (최근 등록된 순서로)
          actionList();
          break;

        case "삭제":
          // 파라미터로 넘어온 id를 사용해 해당 명언 삭제 시도
          actionRemove(rq);
          break;

        // 미지원 명령어는 무시 (추가적으로 안내 메시지를 출력하도록 개선 가능)
      }
    }
  }

  /**
   * 명언 등록
   * - 사용자에게 명언 내용과 작가 이름을 입력받음
   * - 고유 id를 1 증가시켜 새로운 Quotation 객체를 생성 후 리스트에 추가
   */
  void actionWrite() {
    System.out.print("명언 : ");
    String content = scanner.nextLine();     // 명언 본문 입력

    System.out.print("작가 : ");
    String authorName = scanner.nextLine();  // 작가명 입력

    // 새 명언에 부여할 고유번호 생성 (자동 증가)
    lastWiseSayingId++;
    int id = lastWiseSayingId;

    // 명언 데이터 모델 생성 후 저장소에 추가
    Quotation quotation = new Quotation(id, content, authorName);
    quotations.add(quotation);

    // 등록 완료 메시지는 호출부에서 출력하거나 여기서 출력하도록 선택 가능
    // (현재 구현에서는 호출부가 책임지지 않으므로 필요시 출력 추가 가능)
  }

  /**
   * 명언 목록 출력
   * - 헤더 출력 후, 저장소가 비어 있으면 안내 메시지 출력
   * - 비어 있지 않으면 최신 등록된 항목이 위로 오도록 역순으로 출력
   */
  void actionList() {
    System.out.println("번호 / 명언 / 작가");
    System.out.println("--------------------");

    // 저장된 명언이 없을 때 사용자에게 안내
    if (quotations.isEmpty()) {
      System.out.println("등록된 명언이 없습니다.");
    }

    // 가장 최근 등록된 항목부터 거꾸로 출력 (UI상 최근 항목이 위에 보이도록)
    for (int i = quotations.size() - 1; i >= 0; i--) {
      Quotation quotation = quotations.get(i);
      System.out.printf("%d / %s / %s\n",
          quotation.id,
          quotation.content,
          quotation.authorName);
    }
  }

  /**
   * 명언 삭제
   * - Rq로부터 정수 파라미터 "id"를 추출 (없거나 잘못되면 0 반환)
   * - id가 유효하지 않으면 안내 메시지 출력 후 종료
   * - 리스트에서 해당 id를 가진 명언을 찾아 삭제
   * - 삭제 성공/실패에 따라 다른 메시지 출력
   *
   * 명령 예:
   *   "삭제?id=3" -> id = 3인 명언 삭제 시도
   */
  void actionRemove(Rq rq) {
    // 파라미터에서 id 추출 (실패 시 기본값 0)
    int id = rq.getParamAsInt("id", 0);
    if (id == 0) {   // id가 없거나 숫자 변환 실패한 경우
      System.out.println("id를 정확히 입력해주세요.");
      return;
    }

    // 삭제 대상 탐색 (Linear search: 소규모 리스트에 적합)
    Quotation target = null;
    for (Quotation q : quotations) {
      if (q.id == id) {
        target = q;
        break; // 찾았으므로 더 이상 순회하지 않음
      }
    }

    // 대상 존재 여부에 따라 삭제 또는 안내
    if (target != null) {
      quotations.remove(target); // 실제 삭제 수행
      System.out.printf("%d번 명언을 삭제했습니다.\n", id);
    } else {
      System.out.printf("%d번 명언은 존재하지 않습니다.\n", id);
    }
  }
}