-- Test Data for News
INSERT INTO News (id, time, text, title) VALUES
                                             ('1e1a3208-dfc5-4eb7-8d8e-1f50f31dc70a', '2024-01-01 10:00:00+00', 'Breaking news about new tech innovations.', 'Tech Innovations 2024'),
                                             ('be3429c5-daae-4b6e-93fc-dcb1e5c9911b', '2024-01-02 15:30:00+00', 'Sports update: Championship results.', 'Championship Results'),
                                             ('b4c92e1b-334a-46c3-8f4e-58a283a39d78', '2024-01-03 20:15:00+00', 'Discover new recipes for the year.', 'New Year Recipes'),
                                             ('7ac5f6a7-46e5-452e-9340-3545cb4c61f2', '2024-01-04 12:45:00+00', 'Environmental changes and their impact.', 'Environmental Impact'),
                                             ('3f024b2d-90cc-4c92-b9cf-623a5d3f8b64', '2024-01-05 08:20:00+00', 'Top travel destinations of 2024.', 'Travel Destinations'),
                                             ('43e86f88-dae9-4e2a-8125-2d1728eb8d33', '2024-01-06 09:00:00+00', 'Healthcare innovations for a better future.', 'Healthcare Innovations'),
                                             ('58fa9b2e-7a51-4af8-9081-55d4f6358497', '2024-01-07 18:10:00+00', 'Political debates and key decisions.', 'Political Debates'),
                                             ('d2cf8c3a-8dc1-4748-a2d1-6cfaff302ecb', '2024-01-08 22:30:00+00', 'Economic forecasts for the new year.', 'Economic Forecasts'),
                                             ('3e59a07b-d38e-4225-8009-bd88af752f90', '2024-01-09 06:50:00+00', 'Entertainment: Upcoming movie releases.', 'Movie Releases 2024'),
                                             ('847ababe-f6d7-4c1c-90d9-f432d2d7bb37', '2024-01-10 14:00:00+00', 'Tech trends that will shape the future.', 'Future Tech Trends');

-- Test Data for Comment
INSERT INTO Comment (id, time, text, username, news_id) VALUES
                                                            ('28ab736b-3f20-4cd1-bb87-2c05e06ea4ab', '2024-01-01', 'This is an amazing article on tech!', 'user123', '1e1a3208-dfc5-4eb7-8d8e-1f50f31dc70a'),
                                                            ('b71e4c96-9d6f-450d-81c6-3016efddc84e', '2024-01-02', 'Great championship coverage!', 'sportsfan', 'be3429c5-daae-4b6e-93fc-dcb1e5c9911b'),
                                                            ('937e48de-3af4-4f21-a810-cf8c4d2d9f68', '2024-01-03', 'Loved the recipes. Can’t wait to try!', 'chef87', 'b4c92e1b-334a-46c3-8f4e-58a283a39d78'),
                                                            ('57e38bb6-8b4c-496d-8f04-dae5c7be1a26', '2024-01-04', 'Important read on environmental impact.', 'eco_warrior', '7ac5f6a7-46e5-452e-9340-3545cb4c61f2'),
                                                            ('d6e18ae8-02f1-4b18-8e4a-0c18c73450c3', '2024-01-05', 'Can’t wait to visit these destinations!', 'traveler21', '3f024b2d-90cc-4c92-b9cf-623a5d3f8b64'),
                                                            ('8c7a8aef-bcc6-4e3b-a9b9-6e24d8764281', '2024-01-06', 'Healthcare changes are much needed.', 'med_expert', '43e86f88-dae9-4e2a-8125-2d1728eb8d33'),
                                                            ('a6433b76-6e32-4695-83f7-492d978b97f3', '2024-01-07', 'Politics is always a hot topic.', 'politico99', '58fa9b2e-7a51-4af8-9081-55d4f6358497'),
                                                            ('eb60f90c-d92f-4b12-819f-6f3b5b2a6428', '2024-01-08', 'Very informative economic analysis.', 'economist2024', 'd2cf8c3a-8dc1-4748-a2d1-6cfaff302ecb'),
                                                            ('f16c5f3a-4b3c-42f7-87df-724f1085e016', '2024-01-09', 'Excited for the new movies this year!', 'cinemalover', '3e59a07b-d38e-4225-8009-bd88af752f90'),
                                                            ('77de489a-1ef6-40de-bbed-2b8df0a8621c', '2024-01-10', 'Tech trends are always exciting!', 'tech_guru', '847ababe-f6d7-4c1c-90d9-f432d2d7bb37');
INSERT INTO client_name (client_id, username) VALUES
                                                  ('1e1a3208-dfc5-4eb7-8d8e-1f50f31dc70a', 'Admin'),
                                                  ('be3429c5-daae-4b6e-93fc-dcb1e5c9911b', 'Admin'),
                                                  ('b4c92e1b-334a-46c3-8f4e-58a283a39d78', 'Admin'),
                                                  ('7ac5f6a7-46e5-452e-9340-3545cb4c61f2', 'Admin'),
                                                  ('3f024b2d-90cc-4c92-b9cf-623a5d3f8b64', 'Admin'),
                                                  ('43e86f88-dae9-4e2a-8125-2d1728eb8d33', 'Admin'),
                                                  ('58fa9b2e-7a51-4af8-9081-55d4f6358497', 'Admin'),
                                                  ('d2cf8c3a-8dc1-4748-a2d1-6cfaff302ecb', 'Admin'),
                                                  ('3e59a07b-d38e-4225-8009-bd88af752f90', 'Admin'),
                                                  ('847ababe-f6d7-4c1c-90d9-f432d2d7bb37', 'Admin');