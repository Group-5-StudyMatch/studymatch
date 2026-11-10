# 1. Group Formation Context

StudyMatch allows teachers to automatically create groups of students based on their academic competencies and self-assessments.

Before starting the group formation process, the teacher selects the participating students, the matching strategy, and the desired group size.

Each group formation process must store the following information:

- **Name:** an identifier for the formation, such as "Software Quality Project".
- **Selected students:** the list of students selected to participate in the formation.
- **Matching strategy:** COMPLEMENTARY or SIMILAR.
- **Desired group size:** the maximum number of students per group, chosen by the teacher (2, 3, 4, or 5).
- **Creation date:** the time at which the formation was generated.
- **Created by:** the identifier of the teacher responsible for the formation.

StudyMatch must use the size selected by the teacher to calculate the number of groups and distribute eligible students evenly, according to the rules defined in Section 7.

This information makes it possible to identify, consult, and distinguish different group formations, as well as understand the criteria used to create them.

# 2. Student Eligibility

A student is considered eligible if they meet at least one of the following conditions:

- Academic records containing grades.
- A completed onboarding questionnaire.

Students without grades and without a completed onboarding questionnaire are excluded from group formation.

The system must display a warning identifying the excluded students and explaining the reason for their exclusion.

This rule ensures that participating students have some information available for assessing their competencies.

# 3. Representation of Student Competencies

Each student is represented by a vector containing eight competencies.

Scores are normalized to a scale from 0 to 1.

The vector is represented as:

\[
V_i=[s_{i1},s_{i2},\ldots,s_{i8}]
\]

Where:

- \(V_i\) represents the student's vector.
- \(s_{ik}\) represents the student's score in competency \(k\).

## 3.1 Academic Score

When an academic score has been calculated for a given competency, it takes priority.

StudyMatch uses the normalized academic score, obtained from grades and the weights assigned to course units.

For example, a grade of 16 out of 20 corresponds to:

\[
16/20=0.8
\]

## 3.2 Self-Assessment

When no academic score is available for a competency, the level indicated in the onboarding questionnaire is used.

The possible levels are 0, 1, 2, and 3.

Normalization is calculated as follows:

\[
s=\frac{\text{level}}{3}
\]

| Level | Score |
|---|---|
| 0 | 0 |
| 1 | 0.3333… |
| 2 | 0.6666… |
| 3 | 1 |

## 3.3 Competencies with Missing Information

When a student is eligible but has neither academic information nor a self-assessment for a specific competency, that competency will temporarily be assigned a value of 0 during matching calculations.

In the original data, missing information will continue to be represented by `null`, making it possible to distinguish an unknown competency from a competency whose score is actually zero.

This decision allows complete eight-competency vectors to be used in the calculations for the COMPLEMENTARY and SIMILAR strategies without modifying the student's original data.

Temporarily using zero is a known limitation of the algorithm, since missing information does not necessarily mean that the student lacks that competency.

# 4. Matching Strategies

StudyMatch will provide two group formation strategies.

## 4.1 COMPLEMENTARY Strategy

The objective is to create groups whose students have complementary competencies.

The intention is for the stronger competencies of some students to compensate for the less-developed competencies of others.

A group's score is calculated using the highest score among its members for each of the eight competencies.

\[
C(G)=\frac{1}{8}\sum_{k=1}^{8}\max_{i\in G}(s_{ik})
\]

The higher the score, the better the group's competency coverage.

The algorithm should seek a distribution that maximizes the average competency coverage score across groups.

## 4.2 SIMILAR Strategy

The objective is to create groups of students with similar competency levels.

To compare two students, the absolute difference between their scores is calculated for each of the eight competencies.

\[
d(i,j)=\frac{1}{8}\sum_{k=1}^{8}|s_{ik}-s_{jk}|
\]

A group's similarity score is:

\[
S(G)=1-\frac{\sum_{i<j,\ i,j\in G}d(i,j)}{\binom{|G|}{2}}
\]

The denominator represents the number of student pairs in the group.

For four students, there are six pairs.

The closer the score is to 1, the more similar the students are.

The algorithm should seek a distribution that maximizes the average similarity across groups, which is equivalent to minimizing the average differences between group members.

# 5. Group Scores and Weak Competencies

## 5.1 Overall Formation Score

The overall score of a formation is the average of its group scores.

For the COMPLEMENTARY strategy:

\[
C(F)=\frac{1}{g}\sum_{j=1}^{g}C(G_j)
\]

For the SIMILAR strategy:

\[
S(F)=\frac{1}{g}\sum_{j=1}^{g}S(G_j)
\]

For both strategies, the objective is to maximize the corresponding overall score.

## 5.2 Identification of Weak Competencies

The weak-competency threshold is defined as:

**Threshold = 0.5**

A competency is considered a *weak spot* when no student in the group has a score greater than or equal to 0.5 in that competency.

Formally:

\[
\max_{i\in G}(s_{ik})<0.5
\]

For example, if all students have a score of 0.3 in Information Security, that competency is identified as weak.

Identifying these competencies helps explain the formation results and highlight opportunities for improvement.

# 6. Tie-Breaking Criterion

When two distributions have the same overall score, student identifiers are used to ensure deterministic results.

The rule is as follows:

1. Sort the students within each group by ascending ID.
2. Sort the groups by the lowest student ID in each group.
3. Compare the complete sequences of student IDs in the tied formations.
4. Select the formation with the lexicographically smallest sequence.

In this way, given the same data and the same strategy, the algorithm should always produce the same result.

# 7. Number and Size of Groups

StudyMatch allows the teacher to choose the desired group size according to the needs of the academic activity.

The teacher may select groups of 2, 3, 4, or 5 students.

The system must distribute all eligible students evenly, avoiding excessively small groups when a more suitable distribution is possible.

## 7.1 Defining the Desired Group Size

Before generating groups, the teacher selects:

- The students who will participate.
- The matching strategy: COMPLEMENTARY or SIMILAR.
- The desired maximum number of students per group.

The selected size is represented by \(t\).

The number of eligible students is represented by \(n\).

## 7.2 Calculating the Number of Groups

The number of groups is calculated using the following formula:

\[
g=\left\lceil\frac{n}{t}\right\rceil
\]

Where:

- \(g\) represents the number of groups.
- \(n\) represents the number of eligible students.
- \(t\) represents the maximum group size chosen by the teacher.
- \(\lceil\ \rceil\) represents rounding up to the nearest integer.

This formula ensures that enough groups are created to include all students without exceeding the selected maximum group size.

## 7.3 Calculating the Base Group Size

After determining the number of groups, the base size is calculated:

\[
base=\left\lfloor\frac{n}{g}\right\rfloor
\]

Here, \(\lfloor\ \rfloor\) represents rounding down to the nearest integer.

The base size is the minimum number of students each group will have.

## 7.4 Distributing the Remaining Students

When the number of students is not divisible by the number of groups, the remainder is calculated:

\[
r=n\bmod g
\]

The first \(r\) groups receive \(base+1\) students.

The remaining groups receive \(base\) students.

As a result, the difference between the largest and smallest group never exceeds one student.

## 7.5 Example with 10 Students and Groups of 3

The teacher selects:

- Number of students: \(n=10\)
- Desired group size: \(t=3\)

**Step 1 — Number of groups**

\[
g=\left\lceil\frac{10}{3}\right\rceil=4
\]

**Step 2 — Base group size**

\[
base=\left\lfloor\frac{10}{4}\right\rfloor=2
\]

**Step 3 — Remaining students**

\[
r=10\bmod4=2
\]

The first two groups receive three students, and the remaining groups receive two students each.

**Final distribution:**

\[
\boxed{3+3+2+2=10}
\]

This distribution avoids creating an isolated group with only one student.

## 7.6 Example with 23 Students and Groups of 5

The teacher selects:

- Number of students: \(n=23\)
- Desired group size: \(t=5\)

**Number of groups:**

\[
g=\left\lceil\frac{23}{5}\right\rceil=5
\]

**Base group size:**

\[
base=\left\lfloor\frac{23}{5}\right\rfloor=4
\]

**Remainder:**

\[
r=23\bmod5=3
\]

**Final distribution:**

\[
\boxed{5+5+5+4+4=23}
\]

This distribution also satisfies the requirement to form groups of four or five students whenever mathematically possible.

## 7.7 Validating the Distribution

Before generating groups, StudyMatch must verify:

1. That the group size chosen by the teacher is between 2 and 5.
2. That there are at least two eligible students.
3. That the calculated distribution allows groups of at least two students.
4. That each student is assigned to exactly one group.
5. That no group exceeds the selected maximum size.
6. That the difference between group sizes is at most one student.

If these conditions cannot be met, the system must reject the generation process and display an explanatory message to the teacher.

For example, if there are three eligible students and the teacher selects groups of two, the calculated distribution would be 2 + 1. Since groups containing only one student are not allowed, the operation must be rejected.

The system must also reject generation when there are no eligible students, avoiding invalid calculations.

## 7.8 Relationship with Matching Strategies

The calculation of group sizes is independent of the matching strategy.

First, StudyMatch determines how many groups are needed and how many students each group should contain.

Then, it applies the selected strategy:

- **COMPLEMENTARY:** seeks to maximize coverage of the eight competencies within each group.
- **SIMILAR:** seeks to minimize competency differences among students within each group.

Therefore, the teacher can choose the desired group size and the formation strategy independently.

# 8. Complete Example with 8 Students

To demonstrate both matching strategies, consider eight fictional students identified by IDs 1 through 8.

In this example, the teacher selects:

- **Number of eligible students:** 8.
- **Desired maximum group size:** 4 students.
- **Matching strategy:** COMPLEMENTARY or SIMILAR, depending on the objective of the activity.

Each student has scores normalized between 0 and 1 across eight competencies.

## 8.1 Student Data

| ID | C1 | C2 | C3 | C4 | C5 | C6 | C7 | C8 |
|---|---|---|---|---|---|---|---|---|
| 1 | 0.9 | 0.9 | 0.9 | 0.9 | 0.3 | 0.3 | 0.3 | 0.3 |
| 2 | 0.9 | 0.9 | 0.9 | 0.9 | 0.3 | 0.3 | 0.3 | 0.3 |
| 3 | 0.9 | 0.9 | 0.9 | 0.9 | 0.3 | 0.3 | 0.3 | 0.3 |
| 4 | 0.9 | 0.9 | 0.9 | 0.9 | 0.3 | 0.3 | 0.3 | 0.3 |
| 5 | 0.3 | 0.3 | 0.3 | 0.3 | 0.9 | 0.9 | 0.9 | 0.9 |
| 6 | 0.3 | 0.3 | 0.3 | 0.3 | 0.9 | 0.9 | 0.9 | 0.9 |
| 7 | 0.3 | 0.3 | 0.3 | 0.3 | 0.9 | 0.9 | 0.9 | 0.9 |
| 8 | 0.3 | 0.3 | 0.3 | 0.3 | 0.9 | 0.9 | 0.9 | 0.9 |

Students 1 through 4 have stronger competencies in C1–C4, while students 5 through 8 have stronger competencies in C5–C8.

## 8.2 Calculating the Number and Size of Groups

The teacher has chosen a maximum of four students per group.

Therefore:

\[
n=8,\qquad t=4
\]

**Step 1 — Calculate the number of groups**

\[
g=\left\lceil\frac{n}{t}\right\rceil
=\left\lceil\frac{8}{4}\right\rceil=2
\]

**Step 2 — Calculate the base group size**

\[
base=\left\lfloor\frac{n}{g}\right\rfloor
=\left\lfloor\frac{8}{2}\right\rfloor=4
\]

**Step 3 — Calculate the remainder**

\[
r=n\bmod g=8\bmod2=0
\]

Since the remainder is zero, there are no additional students to distribute.

**Result:** two groups of four students.

## 8.3 COMPLEMENTARY Formation

This strategy aims to create groups whose members have complementary competencies.

One optimal distribution is:

- **Group 1:** students 1, 2, 5, and 6.
- **Group 2:** students 3, 4, 7, and 8.

Each group contains two students with strong competencies in C1–C4 and two with strong competencies in C5–C8.

**Group 1 calculation**

The highest score in each competency is:

\[
[0.9;0.9;0.9;0.9;0.9;0.9;0.9;0.9]
\]

Applying the formula:

\[
C(G_1)=\frac{8\times0.9}{8}=0.9
\]

**Group 2 calculation**

Group 2 has the same maximum scores:

\[
C(G_2)=\frac{8\times0.9}{8}=0.9
\]

**Overall formation score**

\[
C(F)=\frac{0.9+0.9}{2}=0.9
\]

Since all competencies have a maximum score above the 0.5 threshold, neither group has any weak competencies.

This distribution achieves the maximum possible competency coverage for the data presented.

## 8.4 SIMILAR Formation

This strategy aims to create groups of students with similar competency profiles.

One optimal distribution is:

- **Group 1:** students 1, 2, 3, and 4.
- **Group 2:** students 5, 6, 7, and 8.

**Group 1 calculation**

Since all four students have exactly the same scores, the distance between any two students is zero.

There are six pairs:

| Pair | Average distance |
|---|---|
| 1–2 | 0 |
| 1–3 | 0 |
| 1–4 | 0 |
| 2–3 | 0 |
| 2–4 | 0 |
| 3–4 | 0 |

Therefore:

\[
S(G_1)=1-\frac{6\times0}{6}=1
\]

**Group 2 calculation**

Students 5, 6, 7, and 8 also have identical profiles.

The six pairs are 5–6, 5–7, 5–8, 6–7, 6–8, and 7–8. All have a distance of zero.

\[
S(G_2)=1-\frac{6\times0}{6}=1
\]

**Overall formation score**

\[
S(F)=\frac{1+1}{2}=1
\]

This distribution achieves the maximum possible similarity.

However, Group 1 has four weak competencies (C5–C8), and Group 2 has four weak competencies (C1–C4).

In total, there are eight occurrences of weak competencies across the two groups.

## 8.5 Comparison of the Strategies

To compare the two distributions, we can also calculate the similarity of the COMPLEMENTARY formation and the competency coverage of the SIMILAR formation.

**Similarity of the COMPLEMENTARY formation**

Each group contains six pairs of students:

- Two pairs with identical profiles, whose distance is 0.
- Four pairs with different profiles, whose distance is 0.6.

The distance between students with different profiles is:

\[
d=\frac{8\times0.6}{8}=0.6
\]

Therefore:

\[
S(G)=1-\frac{2(0)+4(0.6)}{6}=0.6
\]

**Coverage of the SIMILAR formation**

Each group has four competencies with a maximum score of 0.9 and four with a maximum score of 0.3.

\[
C(G)=\frac{4(0.9)+4(0.3)}{8}
\]

\[
C(G)=\frac{3.6+1.2}{8}=0.6
\]

**Comparison table**

| Indicator | COMPLEMENTARY | SIMILAR |
|---|---|---|
| Group 1 | 1, 2, 5, 6 | 1, 2, 3, 4 |
| Group 2 | 3, 4, 7, 8 | 5, 6, 7, 8 |
| Group sizes | 4 and 4 | 4 and 4 |
| Average coverage | 0.90 | 0.60 |
| Average similarity | 0.60 | 1.00 |
| Weak competencies | 0 | 8 |

## 8.6 Example Conclusion

The teacher selected groups of four students, and StudyMatch automatically calculated that two groups would be required.

The COMPLEMENTARY strategy made it possible to create groups with greater competency diversity and coverage.

The SIMILAR strategy made it possible to create groups of students with similar competency profiles.

Both strategies respect the group size chosen by the teacher, include all eligible students, and ensure that each student belongs to exactly one group.

The choice of strategy depends on the objectives of the academic activity.
