package hello.hellospring.controller;
import java.lang.*;
import java.util.*;

import jakarta.servlet.http.HttpServletRequest;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@RestController
public class HelloController {
    private static final Logger log = LoggerFactory.getLogger(HelloController.class);
    private static final String ADMIN_PASSWORD = "2007";
    //데이터 베이스 선택
    public static String scheduleMonth = "schedule_8";
    @Autowired
    private JdbcTemplate jdbcTemplate;

    //사이트 접속
    @GetMapping("/schedule")
    public ResponseEntity<Map<String, Object>>  view(@RequestParam(value = "errorMessage", required = false ) String errorMessage, Model model, HttpServletRequest request){
        String SELECT_ALL_SQL = "SELECT * FROM " + scheduleMonth;

        // 순찰표의 모든 데이터를 가지고 있는 schedule
        List<Map<String, Object>> schedule = jdbcTemplate.queryForList(SELECT_ALL_SQL);

        String clientIP = request.getRemoteAddr();
        log.info("Connected IP: " + clientIP);
        System.out.println("Connected IP : " + clientIP);

        //근무하는 날짜 데이터 가져오기
        List<String> workDays = new ArrayList<>();  //근무 날짜 리스트(내부적으로 배열로 구현된 동적 크기 배열) 컬렉션 자료형
        if (!schedule.isEmpty()) {
            Map<String, Object> firstRow = schedule.get(0);  //전체 데이터 0번째 행
            Set<String> workDaysData = firstRow.keySet();   //모든 키
            int columIndex = 0;
            for (String item : workDaysData) {  //모든 키 List workDays 에 저장
                if (columIndex >= 2) {  // 처음 두개의 값음 id,name 이므로 제외
                    workDays.add(item);
                }
                columIndex++;
            }
        }

        //<table> 데이터 인스턴스
        List<Object>[] tableList = new List[workDays.size()];
        //시간표 0번쨰 행 날짜로 초기화
        for (int i = 0; i < tableList.length; i++) {
            tableList[i] = new ArrayList<>();
            tableList[i].add(workDays.get(i));
        }
        //<table>태그 데이터
        for(int i=0 ; i < workDays.size() ; i++) {
            for (Map<String, Object> row : schedule) {
                Object isWorked = row.get(workDays.get(i));
                if( isWorked != null && isWorked.equals("3.1")) {
                    tableList[i].add(row.get("name"));
                }
            }

            for (Map<String, Object> row : schedule) {
                Object isWorked = row.get(workDays.get(i));
                if( isWorked != null && isWorked.equals("3.2")) {
                    tableList[i].add(row.get("name"));
                }
            }

            for (Map<String, Object> row : schedule) {
                Object isWorked = row.get(workDays.get(i));
                if( isWorked != null && isWorked.equals("3.3")) {
                    tableList[i].add(row.get("name"));
                }
            }
        }

        Map<String, Object> responseData = new HashMap<>();
        responseData.put("errorMessage", errorMessage);
        responseData.put("schedule", schedule);
        responseData.put("workDays", workDays);
        responseData.put("tableList", tableList);


        return ResponseEntity.ok(responseData);
    }

    //근무 변경
    @PostMapping("/workerChange")
    public String set(@RequestBody requestDTO.WorkerChangeRequest request, RedirectAttributes redirectAttributes) {
        String changeDate = request.getChangeDate();
        String fromWorker = request.getFromWorker();
        String toWorker = request.getToWorker();

        if(changeDate == "" || fromWorker == "" || toWorker == "") {
            redirectAttributes.addAttribute("errorMessage", "모든 정보를 입력해주세요.");
            System.out.println("모든 정보를 입력해주세요" + changeDate + fromWorker + toWorker);
        } else if (fromWorker != null && fromWorker.equals(toWorker)) {   // 바꿀 사람과 대타 이름이 같으면 에러
            redirectAttributes.addAttribute("errorMessage", "바꿀 사람과 대신할 사람은 달라야 합니다.");
            System.out.println("바꿀 사람과 대신할 사람은 달라야 합니다");
        } else {
            String isCorrectSql = "SELECT " + changeDate + " FROM " + scheduleMonth + " WHERE name = ? AND " + changeDate + " LIKE '3%'";
            try {
                String isCorrect = jdbcTemplate.queryForObject(isCorrectSql, String.class, fromWorker);
                String sql = "UPDATE " + scheduleMonth + " SET " + changeDate + " = ? WHERE name = ?";
                jdbcTemplate.update(sql, "", fromWorker);
                jdbcTemplate.update(sql, isCorrect, toWorker);
                log.trace("Changed  " + changeDate + " : " + fromWorker + " >>> " + toWorker + " | " + isCorrect);
                System.out.println(changeDate + " : " + fromWorker + " >> " + toWorker);
            } catch (Exception e) { //바꿀려는 날짜에 바꿀 사람이 근무를 안할시 에러
                System.out.println("인원 변경 오류");
                redirectAttributes.addAttribute("errorMessage", "잘못입력함");
            }
        }
        return "redirect:/";
    }

    @PostMapping("/api/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> credentials) {
        if (ADMIN_PASSWORD.equals(credentials.get("password"))) {
            scheduleMonth = credentials.get("month");
            System.out.println("변경 결과 : " + scheduleMonth);
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }


    @GetMapping(value =  {"", "/notice","/list", "/introduce", "/smallbus", "/limousine", "/bigbus", "/request", "/search", "/search/my"})
    public String forward() {

        return "forward:/index.html";
    }
}
