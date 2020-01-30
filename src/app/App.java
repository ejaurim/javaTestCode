package app;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
import java.util.regex.*;

public class App {
    public static void main(String[] args) throws Exception {
        //System.out.println("Hello Java");

        /* 
        * [Java] url 호출, 정보 검색, 정보 가공, 
        *   HttpURLConnection, Pattern, Matcher, StringTokenizer
        *   1. 주어진 url이 정상적으로 호출 되는 지 확인
        *   2. 인스타그램에서 팔로워/팔로잉/포스팅 수가 포함된 문자열 추출
        *   3. 추출된 문자열을 숫자만 분리하여 정수형으로 검사 후 변환
        */

        extractInfoFromInstargramUrl("Your instargramID");

    }

    private static void extractInfoFromInstargramUrl(String strSnsId)
    {
        try {            
            String apiURL = "https://www.instagram.com/" + strSnsId;

            // 주어진 URL을 HttpURLConnection 통해 연결해 본다.
            URL kUrl = new URL(apiURL);
            HttpURLConnection kHttpURLConnection = (HttpURLConnection)kUrl.openConnection();    
            kHttpURLConnection.setRequestMethod("GET");            
            int nResponseCode = kHttpURLConnection.getResponseCode();
			
            if(nResponseCode==200) { // 정상 호출
                
                // 모든 정보가 담긴 버퍼를 보기 좋게 한 라인씩 추출하여 저장한다.
                BufferedReader kBufferedReader;
                kBufferedReader = new BufferedReader(new InputStreamReader(kHttpURLConnection.getInputStream()));
                String nInputLine;
                StringBuffer kBuffer = new StringBuffer();
                while ((nInputLine = kBufferedReader.readLine()) != null) {
                    kBuffer.append(nInputLine);
                    System.out.println("[Log] inputLine:" + nInputLine);
                }
                
                // 보통 아래와 같은 형식에 내용이 저장 되어 있다 이를 불럭으로 본다면 'meta content=' 서 부터 'Posts' 여기까지 일것이다.
                // meta content=\" 5,587 Followers, 619 Following, 997 Posts
                Pattern kPattern = Pattern.compile("meta content=|Posts");      // 검색을 위한 패턴 설정
                Matcher kMatcher = kPattern.matcher(kBuffer);                   // 패턴에서 매치 되는 글자를 검색
                // > [Log] str: meta content=" 5,587 Followers, 619 Following, 997 Posts
                                    
                int nStart = -1;
                int nEnd = -1;
                if(kMatcher.find()) {
                    nStart = kMatcher.end();
                    System.out.println("[Log] m.group :" + kMatcher.group());
                    // > [Log] m.group :meta content=
                }
                if(kMatcher.find()) {
                    nEnd = kMatcher.end();
                    System.out.println("[Log] m.group :" + kMatcher.group());
                    // > [Log] m.group :Posts
                }
        
                // 두 개의 매체이서 검색이 됐다면?
                if(nStart != -1 && nEnd != -1)
                {
                    // 해당 문자만 추출
                    String sTarget = kBuffer.substring(nStart, nEnd);
                    System.out.println("[log] target: " + sTarget);
                    // > [Log] target: " 5,587 Followers, 619 Following, 997 Posts
        
                    // StringTokenizer를 이용한 문자열 분리
                    StringTokenizer kStringTokenizer = new StringTokenizer(sTarget);
                    String sFwersCnt = kStringTokenizer.nextToken("F");     // 팔로워 수 
                    String sFwingCnt = kStringTokenizer.nextToken("F");	    // 필로잉 수
                    String sPostsCnt = kStringTokenizer.nextToken("P");	    // 포스팅 수
                    System.out.println("[log] kStringTokenizer");
                    System.out.println(sFwersCnt); // > "5,587 
                    System.out.println(sFwingCnt); // > ollowers, 619 
                    System.out.println(sPostsCnt); // > Following, 997 
        
                    // 숫자만 추출
                    sFwersCnt = sFwersCnt.substring(sFwersCnt.indexOf("\"") + 1, sFwersCnt.length());       // 공백 이후 끝까지
                    sFwersCnt = sFwersCnt.replace(",", "");                                                 // 천단위 표서 ,가 있다면 제거
                    sFwersCnt = sFwersCnt.replace(" ", "");
                    sFwingCnt = sFwingCnt.substring(sFwingCnt.indexOf(" ") + 1, sFwingCnt.length());
                    sFwingCnt = sFwingCnt.replace(",", "");
                    sFwingCnt = sFwingCnt.replace(" ", "");
                    sPostsCnt = sPostsCnt.substring(sPostsCnt.indexOf(" ") + 1, sPostsCnt.length());
                    sPostsCnt = sPostsCnt.replace(",", "");
                    sPostsCnt = sPostsCnt.replace(" ", "");
                    System.out.println("[log] sPostsCnt.replace");
                    System.out.println(sFwersCnt); // > 5587 
                    System.out.println(sFwingCnt); // > 619 
                    System.out.println(sPostsCnt); // > 997 
                    
                    // 검증, 모두 숫자로만 되어 있는지?
                    for(int i = 1; i < sFwersCnt.length(); i++) {
                        if(!Character.isDigit(sFwersCnt.charAt(i))) { 
                            sFwersCnt = "0";
                            break;
                        }
                    }
                    for(int i = 1; i < sFwingCnt.length(); i++) {
                        if(!Character.isDigit(sFwingCnt.charAt(i))) { 
                            sFwingCnt = "0";
                            break;
                        }
                    }
                    for(int i = 1; i < sPostsCnt.length(); i++) {
                        if(!Character.isDigit(sPostsCnt.charAt(i))) { 
                            sPostsCnt = "0";
                            break;
                        }
                    }
                    
                    System.out.println("[log] info found on Instr, ID: " + strSnsId + ", sFwersCnt: " + sFwersCnt + ", sFwingCnt: " + sFwingCnt + ", sPostsCnt: " + sPostsCnt );

                    int nFwersCnt = Integer.parseInt(sFwersCnt);
                    int nFwingCnt = Integer.parseInt(sFwingCnt);
                    int nPostsCnt = Integer.parseInt(sPostsCnt);

                    System.out.println("[log] info found on Instr, ID: " + strSnsId + ", FwersCnt: " + nFwersCnt + ", FwingCnt: " + nFwingCnt + ", PostsCnt: " + nPostsCnt );
                } else {
                    System.out.println("[error] 정상적으로 호출은 되었으나 패턴에 해당 되는 문자를 찾을 수 없음 ");
                }
                kBufferedReader.close();
            } else {
                System.out.println("[error] 비공개 계정 이거나 url 철자 오류 ");
            }
        
			kHttpURLConnection.disconnect();
        } catch (Exception e) {
            System.out.println("error!!");
            System.out.println(e.toString());
        }       

    }
   
}