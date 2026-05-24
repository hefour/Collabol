package com.collaball.common.config;

import com.collaball.domain.evaluation.repository.EvaluationRepository;
import com.collaball.domain.project.entity.Project;
import com.collaball.domain.project.repository.ProjectRepository;
import com.collaball.domain.task.entity.Task;
import com.collaball.domain.task.entity.TaskAssign;
import com.collaball.domain.task.entity.TaskStatus;
import com.collaball.domain.task.repository.TaskAssignRepository;
import com.collaball.domain.task.repository.TaskRepository;
import com.collaball.domain.team.entity.TeamMember;
import com.collaball.domain.team.repository.TeamMemberRepository;
import com.collaball.domain.user.entity.Department;
import com.collaball.domain.user.entity.User;
import com.collaball.domain.user.repository.UserRepository;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@Profile("test")
@RequiredArgsConstructor
public class TestDataInitializer implements CommandLineRunner {

    private static final String TEST_PASSWORD = "test1234";

    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final TaskRepository taskRepository;
    private final TaskAssignRepository taskAssignRepository;
    private final EvaluationRepository evaluationRepository;
    private final PasswordEncoder passwordEncoder;

    private final List<Long> createdUserIds = new ArrayList<>();
    private final List<Long> createdProjectIds = new ArrayList<>();

    @Override
    @Transactional
    public void run(String... args) {
        log.info("========================================");
        log.info("[TEST] 테스트 데이터 초기화 시작");

        User leader = createUser("test1@soongsil.ac.kr", "장리더", Department.소프트웨어학부);
        User member1 = createUser("test2@soongsil.ac.kr", "김멤버", Department.AI소프트웨어학부);
        User member2 = createUser("test3@soongsil.ac.kr", "이멤버", Department.컴퓨터학부);
        User member3 = createUser("test4@soongsil.ac.kr", "박멤버", Department.글로벌미디어학부);

        Project project = Project.create("콜라볼 테스트 프로젝트", "테스트용 프로젝트입니다.", leader);
        projectRepository.save(project);
        createdProjectIds.add(project.getId());

        teamMemberRepository.save(TeamMember.leader(project, leader));
        teamMemberRepository.save(TeamMember.member(project, member1));
        teamMemberRepository.save(TeamMember.member(project, member2));
        teamMemberRepository.save(TeamMember.member(project, member3));

        Task task1 = taskRepository.save(Task.create(project, "요구사항 분석", "사용자 요구사항을 분석합니다."));
        Task task2 = taskRepository.save(Task.create(project, "백엔드 API 개발", "REST API를 구현합니다."));
        Task task3 = taskRepository.save(Task.create(project, "프론트엔드 개발", "UI를 구현합니다."));
        task2.updateStatus(TaskStatus.IN_PROGRESS);

        taskAssignRepository.save(TaskAssign.of(task1, member1));
        taskAssignRepository.save(TaskAssign.of(task2, leader));
        taskAssignRepository.save(TaskAssign.of(task3, member2));
        taskAssignRepository.save(TaskAssign.of(task3, member3));

        log.info("[TEST] 테스트 계정 4개 생성 완료");
        log.info("[TEST]   test1@soongsil.ac.kr / {} (리더)", TEST_PASSWORD);
        log.info("[TEST]   test2@soongsil.ac.kr / {} (멤버)", TEST_PASSWORD);
        log.info("[TEST]   test3@soongsil.ac.kr / {} (멤버)", TEST_PASSWORD);
        log.info("[TEST]   test4@soongsil.ac.kr / {} (멤버)", TEST_PASSWORD);
        log.info("[TEST] 테스트 프로젝트 생성 완료: {}", project.getName());
        log.info("[TEST] 회원가입 시 이메일 인증 코드는 이메일 대신 서버 로그에 출력됩니다.");
        log.info("========================================");
    }

    @PreDestroy
    @Transactional
    public void cleanup() {
        log.info("[TEST] 테스트 데이터 정리 시작");
        createdProjectIds.forEach(projectId -> {
            evaluationRepository.deleteByProjectId(projectId);
            List<Long> taskIds = taskRepository.findByProjectId(projectId)
                    .stream().map(Task::getId).toList();
            if (!taskIds.isEmpty()) {
                taskAssignRepository.deleteByTaskIdIn(taskIds);
                taskRepository.deleteAllById(taskIds);
            }
            teamMemberRepository.deleteByProjectId(projectId);
            projectRepository.deleteById(projectId);
        });
        createdUserIds.forEach(userRepository::deleteById);
        log.info("[TEST] 테스트 데이터 정리 완료");
    }

    private User createUser(String email, String name, Department department) {
        if (userRepository.existsByEmail(email)) {
            log.warn("[TEST] 이미 존재하는 테스트 계정 스킵: {}", email);
            return userRepository.findByEmail(email).orElseThrow();
        }
        User user = userRepository.save(User.builder()
                .email(email)
                .password(passwordEncoder.encode(TEST_PASSWORD))
                .name(name)
                .department(department)
                .build());
        createdUserIds.add(user.getId());
        return user;
    }
}
