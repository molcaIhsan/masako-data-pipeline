# Airflow Rules
• Maintain Idempotency: Ensure that running the same DAG run multiple times with the same parameters produces the exact same result without duplicating or corrupting data.
• Keep DAG Files Lean: Avoid writing heavy logic, database queries, or API calls inside the global scope of the DAG definition file. Airflow parses these files every few seconds; heavy top-level code slows down the scheduler.
• Use Jinja Templating: Use {{ ds }} (execution date string) or {{ macros }} for dynamic file paths and SQL queries to ensure historical re-runs process the correct logical date.
• Set Explicit Dependencies: Always define clear upstream and downstream relationships using bitshift operators (task1 >> task2) rather than relying on implicit execution order.


---
**Related:** [[gold_tables]] · [[silver_model]] · [[objective]]
