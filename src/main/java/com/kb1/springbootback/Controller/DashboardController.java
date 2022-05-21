package com.kb1.springbootback.controller;

import com.kb1.springbootback.model.calendar.Calendar;
import com.kb1.springbootback.model.medicine.Medicine;
import com.kb1.springbootback.repository.calendar.CalendarRepository;
import com.kb1.springbootback.repository.dashboard.DashboardRepository;
import com.kb1.springbootback.service.CalendarService;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.Map.Entry;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api")
public class DashboardController{

    @Autowired
    private DashboardRepository dashboardRepository;

    @Autowired
    private CalendarRepository calendarRepository;

    @Autowired
    private CalendarService calendarService;

    @PostMapping("/dashboard/recommendMedi")
	public ResponseEntity<?> recommendMedi(@RequestParam(value="id")  String userid) {

        List<Medicine> mediList = dashboardRepository.getRecommendMedi();
        List<Calendar> calendarListtmp = calendarRepository.getByUserid(userid);

        // 모든 약 0으로 초기화
        HashMap<String, Integer> hm = new HashMap<>();
        // 모든 약과 유효성분 set <약 이름, 유효성분>
        HashMap<String, String> ingredientMap = new HashMap<>();
        // 내 약 유효성분 set <약 이름, 유효성분>
        HashMap<String, ArrayList<String>> myIngredientMap = new HashMap<>();
        // 최종 피해야 할 약 리스트 <약 이름, 점수>
        JSONObject jsonob = new JSONObject();
        JSONArray jsonarr = new JSONArray();

        for(Medicine m :mediList){
            hm.put(m.getName(), 0); // 약 이름, score
            ingredientMap.put(m.getName(), m.getIngredient()); // 약 이름, 유효성분
        }
        // 내 약 유효성분 구분자로 나누기
        for(Calendar c : calendarListtmp){
            if(ingredientMap.get(c.getTitle()).length()>6 && ingredientMap.get(c.getTitle())!=null){
                String tmp[] = ingredientMap.get(c.getTitle()).substring(7).split(","); 
                ArrayList<String> myIngredientArray = new ArrayList<>();
                for(String s : tmp){
                    myIngredientArray.add(s);
            }
            myIngredientMap.put(c.getTitle(), myIngredientArray);
        }
        }
        for(Medicine m :mediList){ // 기존 약 
            int cnt = 0;
            for(Calendar c: calendarListtmp){ // 내 약

                // 내 약과 기존 약들의 유효성분 비교
                if(!m.getName().equals(c.getTitle()) && myIngredientMap.get(c.getTitle())!=null){ // 서로 다른 이름의 약만 고려
                    for(String s : myIngredientMap.get(c.getTitle())){
                       if(m.getIngredient()!=null && m.getIngredient().contains(s)){ // 내 약의 유효성분이 기존 약 유효성분에 포함되어 있는지
                            cnt+=2;
                        }
                    }
                }

                // 내 약과 기존 약들의 부작용 비교
                if(!m.getName().equals(c.getTitle())){ // 서로 다른 이름의 약만 고려
                    if(c.getSideEffect_name()!=null && m.getCaution()!=null && m.getCaution().contains(c.getSideEffect_name())){ // 내 약의 유효성분이 기존 약 유효성분에 포함되어 있는지
                        cnt+=1;
                    }
                }

            }
            hm.replace(m.getName(), cnt);
        }

        // Map.Entry 리스트 작성
		List<Entry<String, Integer>> list_entries = new ArrayList<Entry<String, Integer>>(hm.entrySet());

        // 비교함수 Comparator를 사용하여 오름차순으로 정렬
		Collections.sort(list_entries, new Comparator<Entry<String, Integer>>() {
			// compare로 값을 비교
			public int compare(Entry<String, Integer> obj1, Entry<String, Integer> obj2) {
				// 오름 차순 정렬
				return obj2.getValue().compareTo(obj1.getValue());
			}
		});
		// 결과 출력
		for(Entry<String, Integer> entry : list_entries) {
            JSONObject data = new JSONObject();
            data.put("name", entry.getKey());
            data.put("score", entry.getValue());
            jsonarr.add(data);
            if(jsonarr.size()>=10) break; // 10개만 추출
		}
        jsonob.put("avoidList", jsonarr);
		return ResponseEntity.ok(jsonob);
	}

    // @GetMapping("/dashboard/sideEffectMedi")
    // public ResponseEntity<Object> getSideEffectMediPerUser(@RequestParam(value="id") String userid){
    //     System.out.println("\n\ntakingUser\n\n\n");
    //     return ResponseEntity.ok(calendarService.getTakingPerUser(userid));
    // }

    @GetMapping("/dashboard/sideEffectMedi")
    public ResponseEntity<Object> getSideEffectMediPerUser(@RequestParam(value="id") String userid){
        System.out.println("\n\ntakingUser\n\n\n");
        return ResponseEntity.ok(calendarService.getByUserid(userid));
    }

}