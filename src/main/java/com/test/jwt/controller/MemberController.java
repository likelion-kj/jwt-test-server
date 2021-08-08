package com.test.jwt.controller;

import java.util.List;

import com.test.jwt.service.JwtService;
import com.test.jwt.repository.MemberRepository;
import com.test.jwt.util.SaltHasher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.test.jwt.dto.SignUpRequestDTO;
import com.test.jwt.entity.Member;

@RestController
public class MemberController {

    @Autowired
    private JwtService jwtService;

	@Autowired
	private MemberRepository memberRepository;
	
	//TODO 테스트용도, 개발 완료 시 삭제
	@GetMapping("/members")
    @ResponseStatus(HttpStatus.OK)
    public List<Member> findAllMember() {
        return memberRepository.findAll();
    }
	
	
	@GetMapping("/members/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public String signIn(@PathVariable String userId, @RequestParam String password) {
		Member member = memberRepository.findByUserId(userId).orElseThrow(NoMemberOrNotMatchedPasswordException::new);
		if(!member.getHashedPassword().equals(SaltHasher.hash(password + userId, member.getSalt()))) {
			throw new NoMemberOrNotMatchedPasswordException();
		}
        return  jwtService.makeJwt(member);
    }

    @GetMapping("/members/{userId}/auth")
    @ResponseStatus(HttpStatus.OK)
    public void checkJwt(@PathVariable String userId, @RequestParam String token) {
	    jwtService.validateJwt(userId, token);
    }
	
    @PostMapping("/members")
    @ResponseStatus(HttpStatus.CREATED)
    @Transactional
    public String singUp(@RequestBody SignUpRequestDTO requestDTO) {
        if(memberRepository.findByUserId(requestDTO.getId()).isPresent()) {
            throw new MemberAlreadyExistException();
        }
        
        Member member =  new Member();
        member.setUserId(requestDTO.getId());
        // TODO 더 안전하게 생상허면 좋곘지만 이번에는 과제 수준이라서 이렇게 진행
        member.setSalt(String.valueOf(System.currentTimeMillis()));
        String payload = requestDTO.getPassword() + requestDTO.getId();
        member.setHashedPassword(SaltHasher.hash(payload, member.getSalt()));
        member.setName(requestDTO.getName());
        memberRepository.save(member);
        
        return jwtService.makeJwt(member);
    }
	
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public static class NoMemberOrNotMatchedPasswordException extends RuntimeException {}
    
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public static class MemberAlreadyExistException extends RuntimeException {}
    
}
