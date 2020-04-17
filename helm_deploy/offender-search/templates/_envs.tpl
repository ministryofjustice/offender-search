    {{/* vim: set filetype=mustache: */}}
{{/*
Environment variables for web and worker containers
*/}}
{{- define "deployment.envs" }}
env:
  - name: JAVA_OPTS
    value: "{{ .Values.env.JAVA_OPTS }}"

  - name: SPRING_PROFILES_ACTIVE
    value: "elasticsearch"

  - name: JWT_PUBLIC_KEY
    value: "{{ .Values.env.JWT_PUBLIC_KEY }}"

  - name: APPINSIGHTS_INSTRUMENTATIONKEY
    valueFrom:
      secretKeyRef:
        name: {{ template "app.name" . }}
        key: APPINSIGHTS_INSTRUMENTATIONKEY

  - name: AWS_REGION
    value: "eu-west-2"

  - name: ELASTICSEARCH_PORT
    value: "{{ .Values.elasticSearch.port }}"

  - name: ELASTICSEARCH_HOST
    value: "{{ .Values.elasticSearch.host }}"

  - name: ELASTICSEARCH_SCHEME
    value: "{{ .Values.elasticSearch.scheme }}"

  - name: ELASTICSEARCH_ROLE_ARN
    valueFrom:
      secretKeyRef:
        name: offender-search-delius-elastic-search-secret
        key: arn

  - name: ELASTICSEARCH_ROLESESSIONNAME
    valueFrom:
      secretKeyRef:
        name: offender-search-delius-elastic-search-secret
        key: name

{{- end -}}
