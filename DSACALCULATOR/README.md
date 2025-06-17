An interactive arithmetic expression evaluator built in Java that fuses expression parsing, validation, and evaluation with DSA concepts like ArrayList, LinkedList, and Queue. Not just a calculator — this is a guided computation playground with brainy feedback and live correction.

## 🚀 Features

- ✅ **Fully Interactive Console Interface**
  - Accepts infix expressions like `2+(3*5)-4`
  - Live prompts for **corrections** when invalid input is detected
  - Smart handling of **divide-by-zero** — asks what to replace zero with and resumes

- 🧼 **Expression Validation & Correction**
  - Detects and fixes unmatched parentheses with subexpression context
  - Filters out redundant operators (`2++--4` ➝ `2+4`)
  - Asks for fix if parentheses mismatch or operator spam detected

- 🧠 **DSA-Based Internal Representation**
  - Choose your output display:
    - `ArrayList`: Plain and clean
    - `LinkedList`: Styled `a -> b -> c -> null`
    - `Queue`: Partitioned into mini-queues based on capacity

- 🔀 **Infix to Postfix Conversion**
  - Powered by **Shunting Yard Algorithm**
  - Cleanly tokenized postfix list with correct operator precedence

- 🔢 **Even & Odd Number Tracking**
  - Separately maintains and displays all even and odd operands
  - Display adapts to your selected data structure

---

## 📚 Tech Stack

- Java (Core)
- Java Collections (ArrayList, LinkedList, Stack, Queue)
- No external dependencies

---

## 🧪 Sample Expression

```bash
Input: 2+4-5**+6+3+-5
Fixed: 2+4-5*6+3+5

Representation (ArrayList): [2.0, +, 4.0, -, 5.0, *, 6.0, +, 3.0, +, 5.0]  
Postfix: [2.0, 4.0, +, 5.0, 6.0, *, -, 3.0, +, 5.0, +]  
Result: 14.0

Even Numbers: [2.0, 4.0, 6.0]  
Odd Numbers: [5.0, 3.0, 5.0]
