INSERT INTO meetings (title, description, date, start_time, end_time, organizer, status)
SELECT 'Planeringsmöte Q2', 'Genomgång av Q2 mål', '2026-05-10', '09:00', '10:00', 'Anna Svensson', 'PLANNED'
    WHERE NOT EXISTS (SELECT 1 FROM meetings WHERE title = 'Planeringsmöte Q2');

INSERT INTO meetings (title, description, date, start_time, end_time, organizer, status)
SELECT 'Styrelsemöte', 'Kvartalsrapport', '2026-05-15', '13:00', '15:00', 'Erik Karlsson', 'PLANNED'
    WHERE NOT EXISTS (SELECT 1 FROM meetings WHERE title = 'Styrelsemöte');

INSERT INTO meetings (title, description, date, start_time, end_time, organizer, status)
SELECT 'Teamsmöte', 'Veckovis synk', '2026-05-20', '10:00', '10:30', 'Anna Svensson', 'ONGOING'
    WHERE NOT EXISTS (SELECT 1 FROM meetings WHERE title = 'Teamsmöte');

INSERT INTO meetings (title, description, date, start_time, end_time, organizer, status)
SELECT 'Budgetmöte', 'Genomgång av budget', '2026-04-01', '14:00', '16:00', 'Erik Karlsson', 'COMPLETED'
    WHERE NOT EXISTS (SELECT 1 FROM meetings WHERE title = 'Budgetmöte');

INSERT INTO meetings (title, description, date, start_time, end_time, organizer, status)
SELECT 'Kickoff 2026', 'Årets kickoff', '2026-06-01', '09:00', '17:00', 'Anna Svensson', 'PLANNED'
    WHERE NOT EXISTS (SELECT 1 FROM meetings WHERE title = 'Kickoff 2026');