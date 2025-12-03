import java.util.HashMap;
import java.util.Map;

/**
 * Rq (Request Query)
 * - 콘솔에서 입력한 명령 문자열을 파싱해서
 *   액션(명령어)과 파라미터(name=value 쌍)를 관리하는 헬퍼 클래스.
 *
 * 예)
 *   입력: "삭제?id=1&force=true"
 *   - action = "삭제"
 *   - params: ["id" -> "1", "force" -> "true"]
 */
public class Rq {
  // 원본 명령어 전체 문자열 (예: "삭제?id=1")
  private String cmd;

  // 액션(명령어 본체, 예: "삭제")
  private String action;

  // 파라미터 문자열 전체 (예: "id=1&force=true"), 없을 수 있음
  private String queryString;

  // 파라미터를 저장하는 Map
  private Map<String, String> params;

  /**
   * 입력된 명령 문자열을 즉시 파싱하는 생성자.
   * - "?" 기준으로 액션과 쿼리스트링을 분리
   * - "&" 기준으로 파라미터들을 분리
   * - "=" 기준으로 name/value를 분리하여 Map에 저장
   */
  public Rq(String cmd) {
    this.cmd = cmd;
    this.params = new HashMap<>();

    // "?"를 기준으로 액션과 파라미터 영역을 최대 2부분으로 나눔
    String[] cmdBits = cmd.split("\\?", 2);

    // 액션은 "?" 앞의 문자열, 공백 제거
    action = cmdBits[0].trim();

    // "?"가 없는 경우(cmdBits 길이가 1)에는 파라미터가 없으므로 파싱 종료
    if (cmdBits.length == 1) {
      return;
    }

    // "?" 뒤의 전체 파라미터 영역을 획득하고 공백 제거
    queryString = cmdBits[1].trim();

    // 파라미터들을 "&"로 분리
    String[] queryStringBits = queryString.split("&");

    // 각 파라미터 문자열을 "name=value" 형태로 파싱
    for (String queryParamStr : queryStringBits) {
      String[] queryParamStrBits = queryParamStr.split("=", 2);

      // 잘못된 형식(예: "id" 또는 "id=")은 무시
      if (queryParamStrBits.length < 2) continue;

      String paramName = queryParamStrBits[0].trim();
      String paramValue = queryParamStrBits[1].trim();

      // Map에 저장
      params.put(paramName, paramValue);
    }
  }

  /** 액션(명령어 본체)을 반환 */
  public String getAction() {
    return action;
  }

  /** 문자열 파라미터 가져오기 */
  public String getParam(String paramName, String defaultValue) {
    return params.getOrDefault(paramName, defaultValue);
  }

  /** 정수형 파라미터를 안전하게 가져오기 */
  public int getParamAsInt(String paramName, int defaultValue) {
    String paramValue = params.get(paramName);
    if (paramValue == null) return defaultValue;

    try {
      return Integer.parseInt(paramValue);
    } catch (NumberFormatException e) {
      return defaultValue;
    }
  }
}