FROM locustio/locust

# # Add the external tasks directory into /tasks
ADD locust-tasks /locust-tasks

# Expose the required Locust ports
EXPOSE 5557 5558 8089

# Set script to be executable
USER root
RUN chmod 755 /locust-tasks/run.sh

# Start Locust using LOCUS_OPTS environment variable
USER locust
ENTRYPOINT ["/locust-tasks/run.sh"] 
