```text
graph TD
subgraph ParentJob [👨‍💼 ParentJob (부모)]
direction TB
Step1[JobStep] --> Step2[step2]

        subgraph Step1Detail [JobStep 내부]
            ChildJob[👶 ChildJob (자식)]
        end
    end
```

📦 그림으로 보는 JobStep 데이터 전달 (택배 배송)
이 코드는 부모가 자식에게 `이름표(user1)`를 붙여서 심부름을 보내는 과정입니다.

### 1. 준비 단계 (ParentJob)
부모 Job이 실행되면서 JobStep에 도착합니다. 여기서 데이터를 준비합니다.

배경: 부모는 자식에게 일을 시키고 싶음.

행동: `리스너(StepExecutionListener)`가 몰래 `주머니(ExecutionContext)`에 데이터를 넣음.

"자, 여기 `name`이라는 주머니에 `user1`이라고 적어서 넣어둘게."

### 2. 변환 단계 (Extractor)
이제 자식 Job을 부르기 직전, 주머니에 있는 데이터를 `공식 문서(Parameter)`로 바꿉니다.

| 구분 | 변경 전 (ExecutionContext)                     | 변경 후 (JobParameters) |
|----|---------------------------------------------|----------------------|
| 형태 | 임시 메모장 (Map)                                | 공식 작업 지시서            
| 내용 | {"name": "user1"}                           | name=user1           |
| 역할 | DefaultJobParametersExtractor가 이 변환 작업을 수행함 |

### 3. 실행 단계 (ChildJob)
자식 Job은 변환된 `공식 문서(JobParameters)`를 들고 출발합니다.

실행 명령: ChildJob아, 시작해!

지참물: name=user1 (아까 변환한 것)

결과: 자식 Job은 `아, 내 이름 파라미터가 user1이구나`라고 인식하고 Step1을 실행함.
