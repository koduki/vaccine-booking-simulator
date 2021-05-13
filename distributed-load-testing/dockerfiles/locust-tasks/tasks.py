from locust import HttpUser, TaskSet, task, between, constant

class UserBehavior(TaskSet):
    @task(3)
    def show(self):
        res = self.client.get("/book")
        # debug
        print("GET /book: " + res.content.decode())

    @task(1)
    def book(self):
        print(self.get_date())
        res = self.client.post("/book", json={"name": "koduki", "date":self.get_date()})
        # debug
        print("POST /book: " + res.content.decode())
    
    def get_date(self):
        return "0602"

class WebsiteUser(HttpUser):
    tasks = {UserBehavior:1}
    wait_time = constant(0)