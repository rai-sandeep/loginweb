INSERT INTO role VALUES (1,'ADMIN')
ON CONFLICT (role_id)
DO NOTHING;