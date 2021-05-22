FROM locustio/locust

# # Add the external tasks directory into /tasks
ADD locust-scripts /locust
ADD ./run.sh /locust/run.sh

# Expose the required Locust ports
EXPOSE 5557 5558 8089

# Set script to be executable
USER root
RUN chmod 755 /locust/run.sh

# Start Locust using LOCUS_OPTS environment variable
USER locust
ENTRYPOINT ["/locust/run.sh"] 
