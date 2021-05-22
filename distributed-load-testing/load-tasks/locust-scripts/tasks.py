from locust import HttpUser, TaskSet, task, between, constant
import random, string

class UserBehavior(TaskSet):
    @task(3)
    def show(self):
        res = self.client.get("/book")
        # debug
        # print("GET /book: " + res.content.decode())

    @task(1)
    def book(self):
        print(self.get_date())
        res = self.client.post("/book", json={"name": self.get_user(), "date":self.get_date()})
        # debug
        # print("POST /book: " + res.content.decode())
    
    def get_date(self):
        day="{d:02d}"
        return "2021-06-" + day.format(d = random.randrange(1, 30))

    def get_user(self):
        length = 20
        randlst = [random.choice(string.ascii_letters + string.digits) for i in range(length)]
        return ''.join(randlst)

class WebsiteUser(HttpUser):
    tasks = {UserBehavior:1}
    wait_time = constant(0)