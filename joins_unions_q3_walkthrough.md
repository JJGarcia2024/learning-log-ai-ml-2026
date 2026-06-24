# Q3 — Initial Questions and Answers, Part 2 (Walkthrough)

> A plain-English breakdown of what the problem is asking and the SQL mechanics
> needed to solve it. **No solution query here** — just the reasoning toolkit.

---

## 1. What the question wants you to accomplish

### The big shift from Part 1

- **Part 1:** the universe of people was defined by *who asked or answered*.
- **Part 2:** the universe flips — it's defined by *who **signed up** in January 2019*,
  regardless of whether they ever posted anything.

So the `users` table becomes the **anchor** (starting point). Questions and answers
are extra info you attach to those people *only if it exists*.

### The output columns

| Column | Meaning |
|--------|---------|
| `id` | Every user whose account `creation_date` is in Jan 2019 (Jan 1–31 inclusive). Everyone here must appear in the output. |
| `q_creation_date` | Date of the user's **very first question ever**. `NULL` if they never asked. |
| `a_creation_date` | Date of the user's **very first answer ever**. `NULL` if they never answered. |

> ⚠️ **Typo in the prompt:** it describes `a_creation_date` as "first time the user
> posted a question." It clearly means **answer**.

### The three rules that drive everything

1. **All January-2019 signups must be included** — even total lurkers who never asked
   or answered. The `users` list must never shrink.
2. **Questions/answers posted *after* Jan 31 still count.** The January date filter
   applies **only to when the user *joined***, NOT to when they posted. A user who
   joined Jan 15, 2019 and asked their first question in March 2020 — that question
   still counts. So do **not** filter on the question/answer dates.
3. **"First" question and "first" answer** → a `MIN(...)` on the respective creation
   dates, grouped per user.

### The structural picture

```
users  ──(user id)──►  posts_questions
   │
   └────(user id)──►  posts_answers
```

- `users` is the center/anchor.
- Attach each user's questions by matching user IDs.
- Attach each user's answers by matching user IDs.
- Two **separate** links off the same `users` table — not a chain.

### Mental gotchas

- **Anchor = users**, not questions/answers (opposite instinct from Part 1).
- **The date window is about *joining*, not posting.**
- **Don't lose the lurkers** — the join direction must protect the full users list.
- **Two separate links** off the same `users` table.

---

## 2. Which JOIN types satisfy Rule #1

Goal of Rule #1: **the `users` list must never shrink.** Every Jan-2019 signup has to
survive — matched or not.

Assume the query is written as `FROM users <JOIN> posts_questions`, so `users` is the
**left** table and `posts_questions` is the **right** table.

| JOIN type | Verdict | Why |
|-----------|---------|-----|
| `INNER JOIN` | ❌ fails | Keeps a user only if a matching question exists. Lurkers get dropped — the same bug as Question 1. |
| `RIGHT JOIN` | ❌ fails | Preserves all rows from the **right** table (questions) and drops unmatched **left** rows (users). Wrong table protected. |
| `LEFT JOIN`  | ✅ works | Preserves all rows from the **left** table (users); fills `NULL` where there's no matching question. Every signup survives. |
| `FULL JOIN`  | ⚠️ over-delivers | Keeps unmatched rows from **both** sides, so it also drags in posts from users who did NOT join in January (rows with `NULL` id). Pollutes the target universe. |

### Takeaway

> **Put `users` first (left), then `LEFT JOIN` outward to questions and answers.**

```
FROM users
LEFT JOIN posts_questions  ON ...
LEFT JOIN posts_answers    ON ...
```

Each `LEFT JOIN` means: *"keep every user I already have; attach question/answer info
only if it exists, otherwise leave NULL."*

**Two clarifications:**

1. **"Left" is positional, not magic.** `LEFT` only preserves users *because users is
   written first*. Flip the order and the same keyword would protect a different table.
   The real rule is "preserve the anchor table."
2. **Both joins must be the preserving kind.** The moment one link becomes `INNER`
   (or you filter post dates in a way that nukes NULLs), you lose the lurkers.

---

## 3. The `MIN()` + `GROUP BY` interaction

### The problem they solve

After joining users → questions → answers, you get an **exploded** table: one row per
(user × question × answer) combination. A user with 3 questions and 5 answers can
occupy up to 15 rows. You want **one row per user**, showing their earliest question
and earliest answer. `GROUP BY` + `MIN()` do that collapse.

### Step 1 — `GROUP BY` makes the buckets

`GROUP BY u.id` collapses all rows sharing the same user id into a single output row.
Output rows = number of distinct users.

### Step 2 — `MIN()` picks one value per bucket

Once grouped, every other selected column must be **aggregated**. `MIN()` on a date
column = *"the earliest date in this bucket."*

- `MIN(q.creation_date)` → the user's **first-ever question** date.
- `MIN(a.creation_date)` → the user's **first-ever answer** date.

"First time they did X" = the smallest (earliest) timestamp → `MIN`.

### Step 3 — how NULLs behave (the important part)

> **`MIN()` ignores NULLs. But if *every* value in the group is NULL, `MIN()` returns NULL.**

- Never-asked user → all question dates NULL → `MIN(q.creation_date)` = **NULL** ✓
- Asked one or many times → `MIN` skips NULL padding, returns the genuine earliest date ✓

The `LEFT JOIN` **creates** the NULLs for non-participants; `MIN()` **preserves** them
into the final output. Together they satisfy: "if the user never posted, the value
should be null."

### The subtlety: group by `u.id`, not the post tables

Group on the **users** id (the anchor), because the universe is users. Grouping on a
question id would put you back to one-row-per-question and lose the lurkers. The anchor
drives the grouping just like it drove the joins.

---

## 4. Checklist before writing the query

- [ ] `FROM users` as the anchor (left table).
- [ ] `LEFT JOIN` to `posts_questions` and `LEFT JOIN` to `posts_answers` (two separate links on user id).
- [ ] `WHERE` filters the **user's** `creation_date` to Jan 2019 — **not** the post dates.
- [ ] `SELECT id, MIN(question date) AS q_creation_date, MIN(answer date) AS a_creation_date`.
- [ ] `GROUP BY` the user id.
- [ ] Confirm lurkers survive with NULL post dates, and post-Jan-31 posts are still counted.
