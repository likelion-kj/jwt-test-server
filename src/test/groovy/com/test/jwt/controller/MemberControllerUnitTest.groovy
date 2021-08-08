package com.test.jwt.controller


import com.test.jwt.entity.Member
import com.test.jwt.service.JwtService
import com.test.jwt.dto.SignUpRequestDTO
import com.test.jwt.repository.MemberRepository
import com.test.jwt.util.SaltHasher
import spock.lang.Specification

class MemberControllerUnitTest extends Specification {
    def "SignIn. don't find user id "() {
        MemberController controller = new MemberController()
        MemberRepository memberRepository = Mock()
        controller.memberRepository = memberRepository

        given:
        String userId =  "userId"
        String password = "password"

        and:
        memberRepository.findByUserId("userId") >> Optional.empty()

        when:
        controller.signIn(userId, password)

        then:
        thrown(MemberController.NoMemberOrNotMatchedPasswordException)

    }
    def "SignIn. don't math password"() {
        MemberController controller = new MemberController()
        MemberRepository memberRepository = Mock()
        controller.memberRepository = memberRepository

        given:
        String userId = "userId"
        String password = "password"


        and:
        Member member = new Member()
        memberRepository.findByUserId("userId") >> Optional .of(member)
        member.setSalt("salt")
        member.setHashedPassword(SaltHasher.hash("not_matched_password" + "userId", member.getSalt()))

        when:
        controller.signIn(userId, password)

        then:
        thrown(MemberController.NoMemberOrNotMatchedPasswordException)
    }
    def "SignIn. success"() {
        MemberController controller = new MemberController()
        MemberRepository memberRepository = Mock()
        JwtService jwtService = Mock()
        controller.memberRepository = memberRepository
        controller.jwtService = jwtService

        given:
        String userId = "userId"
        String password = "password"

        and:
        Member member = new Member()
        memberRepository.findByUserId("userId") >> Optional .of(member)
        member.setSalt("salt")
        member.setHashedPassword(SaltHasher.hash("password" + "userId", member.getSalt()))

        when:
        controller.signIn(userId, password)

        then:
        1 * jwtService.makeJwt(member)
    }

    def "SingUp. find userId"() {
        MemberController controller = new MemberController()
        MemberRepository memberRepository = Mock()
        controller.memberRepository = memberRepository

        given:
        SignUpRequestDTO requestDTO = new SignUpRequestDTO()
        requestDTO.setId("userId")
        requestDTO.setPassword("password")
        requestDTO.setName("name")

        and:
        Member member = new Member()
        memberRepository.findByUserId("userId") >> Optional.of(member)

        when:
        controller.singUp(requestDTO)

        then:
        thrown(MemberController.MemberAlreadyExistException)
    }
    def "SingUp. success"() {
        MemberController controller = new MemberController()
        MemberRepository memberRepository = Mock()
        JwtService jwtService = Mock()
        controller.memberRepository = memberRepository
        controller.jwtService = jwtService

        given:
        SignUpRequestDTO requestDTO = new SignUpRequestDTO()
        requestDTO.setId("userId")
        requestDTO.setPassword("password")
        requestDTO.setName("name")

        and:
        memberRepository.findByUserId("userId") >> Optional.empty()

        when:
        controller.singUp(requestDTO)

        then:
        1 * memberRepository.save(_)
        1 * jwtService.makeJwt(_)
    }

}